package com.king.lms.e_learning_hub.service.Payment;

import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import com.king.lms.e_learning_hub.configuration.PayOSProperties;
import com.king.lms.e_learning_hub.dto.payment.OrderPreparedDto;
import com.king.lms.e_learning_hub.dto.payment.LegacyPayOsCallbackRequest;
import com.king.lms.e_learning_hub.dto.payment.PayOsCallbackRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentResponse;
import com.king.lms.e_learning_hub.enums.PaymentStatusResult;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.payos.PayOS;
import vn.payos.exception.APIException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PAYOSPaymentService implements PaymentGateway {

    private final OrderDbProcessor orderDbProcessor; // Service phụ trách DB riêng biệt
    private final PayOS payOS;
    private final PayOSProperties payOSProperties;

    public PAYOSPaymentService(OrderDbProcessor orderDbProcessor,
            PayOS payOS,
            PayOSProperties payOSProperties) {
        this.orderDbProcessor = orderDbProcessor;
        this.payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(),
                payOSProperties.getChecksumKey());
        this.payOSProperties = payOSProperties;
    }

    @Override
    public PaymentResponse createPaymentLink(PaymentRequest request) throws APIException {
        if (request == null || request.getCourseId() == null || request.getCodePaymentMethod() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // 1. Thực hiện xử lý Database biệt lập (Transaction đóng/ngắt ngay sau khi hàm
        // này chạy xong)
        OrderPreparedDto preparedData = orderDbProcessor.processOrderAndTransactionInDb(request);

        // 2. Chuyển đổi số tiền sang đơn vị của PayOS (Quy chuẩn VND không lấy phần
        // thập phân)
        long amountForGateway = preparedData.getTotalAmount().setScale(0, RoundingMode.HALF_UP).longValue();

        // 3. Đóng gói Item theo tiêu chuẩn SDK PayOS
        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(preparedData.getCourseTitle())
                .quantity(1)
                .price(amountForGateway)
                .build();

        // Tránh trùng mã orderCode của PayOS khi đơn cũ mang trạng thái PROCESSING
        long uniquePayOsOrderCode = preparedData.getOrderId() * 100_000L + (long) (Math.random() * 90_000L + 10_000L);

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(uniquePayOsOrderCode)
                .description("DH" + preparedData.getOrderId())
                .amount(amountForGateway)
                .item(item)
                .returnUrl(payOSProperties.getReturnUrl())
                .cancelUrl(payOSProperties.getCancelUrl())
                .build();

        try {
            // 4. Thực hiện cuộc gọi mạng (Network Call) sang cổng PayOS bên ngoài
            // Transaction
            log.info("Calling PayOS API for Order ID: {}, PayOS Code: {}", preparedData.getOrderId(),
                    uniquePayOsOrderCode);
            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

            return PaymentResponse.builder()
                    .checkoutUrl(data.getCheckoutUrl())
                    .orderCode(data.getOrderCode())
                    .transactionCode(preparedData.getTransactionCode())
                    .build();

        } catch (APIException ex) {
            log.error("PayOS gateway communication failed for Order ID: {}", preparedData.getOrderId(), ex);
            throw new AppException(ErrorCode.PAYMENT_GATEWAY_ERROR);
        }
    }

    @Override
    public String checkPaymentStatus(Long orderCode) throws APIException {
        // Logic truy vấn trạng thái từ PayOS mạng (Không dùng @Transactional)
        PaymentLink paymentLink = payOS.paymentRequests().get(orderCode);
        return paymentLink.getStatus().getValue();
    }

    @Override
    public PaymentStatusResult updatePaymentStatus(PayOsCallbackRequest request) {
        if (request == null || request.getData() == null || request.getData().getOrderCode() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        boolean verifyRequest = verifySignature(request.getData(), request.getSignature(),
                payOSProperties.getChecksumKey());

        if (!verifyRequest) {
            return PaymentStatusResult.FAILED;
        }

        LegacyPayOsCallbackRequest legacyRequest = mapToLegacyCallbackRequest(request.getData());

        // PAYOS-specific callback handling: decide the result first, then delegate DB
        // write
        if (isSuccessfulCallback(legacyRequest)) {
            orderDbProcessor.updateSuccessPaymentStatusInDb(legacyRequest);
            return PaymentStatusResult.SUCCESS;
        }

        orderDbProcessor.updateFailedPaymentStatusInDb(legacyRequest);
        return PaymentStatusResult.FAILED;
    }

    private boolean verifySignature(WebhookData data, String expectedSignature, String checksumKey) {
        try {
            // Đưa các trường cần băm của WebhookData vào TreeMap để tự động sắp xếp key
            // theo alphabet (A-Z)
            Map<String, Object> params = new TreeMap<>();
            params.put("amount", data.getAmount());
            params.put("accountNumber", data.getAccountNumber() != null ? data.getAccountNumber() : "");
            params.put("code", data.getCode());
            params.put("currency", data.getCurrency() != null ? data.getCurrency() : "");
            params.put("description", data.getDescription());
            params.put("desc", data.getDesc() != null ? data.getDesc() : "");
            params.put("orderCode", data.getOrderCode());
            params.put("paymentLinkId", data.getPaymentLinkId() != null ? data.getPaymentLinkId() : "");
            params.put("reference", data.getReference() != null ? data.getReference() : "");
            params.put("transactionDateTime",
                    data.getTransactionDateTime() != null ? data.getTransactionDateTime() : "");

            params.put("counterAccountBankId",
                    data.getCounterAccountBankId() != null ? data.getCounterAccountBankId() : "");
            params.put("counterAccountBankName",
                    data.getCounterAccountBankName() != null ? data.getCounterAccountBankName() : "");
            params.put("counterAccountName", data.getCounterAccountName() != null ? data.getCounterAccountName() : "");
            params.put("counterAccountNumber",
                    data.getCounterAccountNumber() != null ? data.getCounterAccountNumber() : "");

            params.put("virtualAccountName", data.getVirtualAccountName() != null ? data.getVirtualAccountName() : "");
            params.put("virtualAccountNumber",
                    data.getVirtualAccountNumber() != null ? data.getVirtualAccountNumber() : "");

            // Tạo chuỗi raw data dạng: key1=value1&key2=value2...
            String rawDataStr = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            // Thực hiện băm HMAC-SHA256 với Checksum Key hệ thống
            String calculatedSignature = new HmacUtils("HmacSHA256", checksumKey).hmacHex(rawDataStr);

            return calculatedSignature.equals(expectedSignature);
        } catch (Exception e) {
            log.error("Lỗi trong quá trình tính toán chữ ký số", e);
            return false;
        }
    }

    private LegacyPayOsCallbackRequest mapToLegacyCallbackRequest(WebhookData request) {
        return LegacyPayOsCallbackRequest.builder()
                .orderCode(request.getOrderCode())
                .paymentStatusCode(request.getCode())
                .status("00".equals(request.getCode()) ? "PAID" : "FAILED")
                .build();
    }

    private boolean isSuccessfulCallback(LegacyPayOsCallbackRequest request) {
        return "00".equals(request.getPaymentStatusCode())
                && "PAID".equalsIgnoreCase(request.getStatus());
    }

}
