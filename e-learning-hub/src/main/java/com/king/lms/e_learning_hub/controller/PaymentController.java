package com.king.lms.e_learning_hub.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.payment.PayOsCallbackRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentResponse;
import com.king.lms.e_learning_hub.enums.PaymentStatusResult;
import com.king.lms.e_learning_hub.service.Payment.PaymentGateway;
import com.king.lms.e_learning_hub.service.Payment.PaymentGatewayFactory;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/payments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentGatewayFactory paymentGatewayFactory;

    @PostMapping("/create-link")
    public ApiResponse<PaymentResponse> createPaymentLink(@Valid @RequestBody PaymentRequest request){

        try {
            PaymentGateway gateway = paymentGatewayFactory.getGateway(request.getCodePaymentMethod());
            PaymentResponse response = gateway.createPaymentLink(request);
            return ApiResponse.<PaymentResponse>builder()
            .result(response)
            .build();
        } catch (IllegalArgumentException ex) {
            // Bắt trường hợp Client truyền sai paymentMethod không có trong Map hệ thống
            log.error("Phương thức thanh toán không hợp lệ: {} loi: {}", request.getCodePaymentMethod(),ex.getMessage());
            throw ex; 
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi khởi tạo cổng thanh toán công nghệ: ", ex);
            throw new RuntimeException("Không thể khởi tạo luồng giao dịch. Vui lòng thử lại sau.");
        }
    }

    @PostMapping("/callback/payos")
    public ApiResponse<PaymentStatusResult> updatePaymentStatus(@Valid @RequestBody PayOsCallbackRequest request) {
        try {
            PaymentGateway gateway = paymentGatewayFactory.getGateway("PAYOS");
            PaymentStatusResult result = gateway.updatePaymentStatus(request);

            return ApiResponse.<PaymentStatusResult>builder()
                    .message(result.getMessage())
                    .result(result)
                    .build();
        } catch (IllegalArgumentException ex) {
            log.error("Phương thức thanh toán callback không hợp lệ: {} lỗi: {}", "PAYOS", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật trạng thái thanh toán: ", ex);
            throw new RuntimeException("Không thể cập nhật trạng thái thanh toán. Vui lòng thử lại sau.");
        }
    }


    
}
