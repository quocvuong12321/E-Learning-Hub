package com.king.lms.e_learning_hub.service.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.king.lms.e_learning_hub.dto.payment.LegacyPayOsCallbackRequest;
import com.king.lms.e_learning_hub.dto.payment.OrderPreparedDto;
import com.king.lms.e_learning_hub.dto.payment.PaymentRequest;
import com.king.lms.e_learning_hub.entity.Course;
import com.king.lms.e_learning_hub.entity.Order;
import com.king.lms.e_learning_hub.entity.Order.OrderStatus;
import com.king.lms.e_learning_hub.entity.Transaction.TransactionStatus;
import com.king.lms.e_learning_hub.entity.PaymentMethod;
import com.king.lms.e_learning_hub.entity.Transaction;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.repository.CourseRepository;
import com.king.lms.e_learning_hub.repository.OrderRepository;
import com.king.lms.e_learning_hub.repository.PaymentMethodRepository;
import com.king.lms.e_learning_hub.repository.TransactionRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.service.UserCourseService;
import com.king.lms.e_learning_hub.util.JwtUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDbProcessor {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final UserCourseService userCourseService;

    @Transactional
    public OrderPreparedDto processOrderAndTransactionInDb(PaymentRequest request) {
        // 1. Kiểm tra thực thể hợp lệ
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        PaymentMethod paymentMethod = paymentMethodRepository.findByCode(request.getCodePaymentMethod())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXIST));

        Long userId = jwtUtils.getCurrentUserId();

        // 2. Tính toán tài chính chính xác tuyệt đối cho tiền tệ VND (Scale = 0)
        double discountDouble = (course.getDiscount() == null) ? 0.0 : course.getDiscount();
        BigDecimal discountFraction = BigDecimal.valueOf(discountDouble).divide(BigDecimal.valueOf(100), 6,
                RoundingMode.HALF_UP);
        BigDecimal discountAmount = course.getPrice().multiply(discountFraction).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalAmount = course.getPrice().subtract(discountAmount).setScale(0, RoundingMode.HALF_UP);

        // 3. Kiểm tra hoặc tạo mới Order (Áp dụng logic: Nếu đơn cũ bị HỦY -> Tạo đơn
        // mới độc lập)
        Order order = validateOrCreateOrder(course, userId, paymentMethod, discountAmount, totalAmount);

        // 4. Khởi tạo / Tái sử dụng bản ghi Transaction nội bộ hệ thống ở trạng thái
        // PROCESSING
        Transaction transaction = getOrCreateTransaction(order, request.getCodePaymentMethod(), totalAmount);

        // 5. Trả về DTO đóng gói dữ liệu sạch
        return OrderPreparedDto.builder()
                .orderId(order.getId())
                .courseTitle(course.getTitle())
                .totalAmount(totalAmount)
                .transactionCode(transaction.getTransactionCode())
                .build();
    }

    @Transactional
    public void updateSuccessPaymentStatusInDb(LegacyPayOsCallbackRequest request) {
        Transaction transaction = loadProcessingTransaction(request);
        if (transaction == null) {
            return;
        }

        Order order = transaction.getOrder();
        transaction.setRawResponse(buildRawResponse(request));
        applySuccessStatus(transaction, order, request);
        persistPaymentState(transaction, order);

        userCourseService.createUserCourseFromPaidOrder(order);
    }

    @Transactional
    public void updateFailedPaymentStatusInDb(LegacyPayOsCallbackRequest request) {
        Transaction transaction = loadProcessingTransaction(request);
        if (transaction == null) {
            return;
        }

        Order order = transaction.getOrder();
        transaction.setRawResponse(buildRawResponse(request));
        applyFailedStatus(transaction, order, request);
        persistPaymentState(transaction, order);
    }

    private Order validateOrCreateOrder(Course course, Long userId, PaymentMethod paymentMethod,
            BigDecimal discountAmount, BigDecimal totalAmount) {
        Order existedOrder = orderRepository.findByCourseIdAndUserId(course.getId(), userId);

        if (existedOrder != null) {
            if (existedOrder.isCompleted()) {
                throw new AppException(ErrorCode.ORDER_COMPLETED);
            }

            Optional<Transaction> existingTransaction = findExistingTransaction(existedOrder);
            if (existingTransaction.isPresent()) {
                Transaction tx = existingTransaction.get();
                if (tx.getStatus() == TransactionStatus.SUCCESS) {
                    throw new AppException(ErrorCode.TRANSACTION_ALREADY_EXISTS);
                }
                if (tx.getStatus() == TransactionStatus.PROCESSING) {
                    return existedOrder;
                }
            }

            // ĐƠN CŨ BỊ HỦY: Bỏ qua (bypass), trôi xuống tầng dưới để sinh một bản ghi mới
            // hoàn toàn
            if (!existedOrder.isCancelled()) {
                return existedOrder;
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Order newOrder = Order.builder()
                .course(course)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .user(user)
                .orderCode(UUID.randomUUID().toString())
                .build();

        return orderRepository.save(newOrder);
    }

    private Transaction getOrCreateTransaction(Order order, String paymentMethodCode, BigDecimal amount) {
        Optional<Transaction> existingTxnOpt = findExistingTransaction(order);
        if (existingTxnOpt.isPresent()) {
            Transaction existingTxn = existingTxnOpt.get();
            if (existingTxn.getStatus() == TransactionStatus.PROCESSING) {
                existingTxn.setAmount(amount);
                existingTxn.setPaymentMethod(paymentMethodCode);
                return transactionRepository.save(existingTxn);
            }
        }

        Transaction newTransaction = Transaction.builder()
                .order(order)
                .transactionCode(UUID.randomUUID().toString())
                .paymentMethod(paymentMethodCode)
                .amount(amount)
                .status(TransactionStatus.PROCESSING)
                .build();
        return transactionRepository.save(newTransaction);
    }

    private Transaction loadProcessingTransaction(LegacyPayOsCallbackRequest request) {
        if (request == null || request.getOrderCode() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Long orderId = request.getOrderCode() / 100_000L;

        return transactionRepository.findByOrderIdAndStatus(orderId, TransactionStatus.PROCESSING)
                .orElseGet(() -> {
                    log.warn("Transaction cho Order ID {} không tồn tại hoặc đã được xử lý trước đó.", orderId);
                    return null;
                });
    }

    private String buildRawResponse(LegacyPayOsCallbackRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Không thể parse cấu trúc Webhook sang JSON string cho orderCode: {}", request.getOrderCode(), e);
            return "{ \"error\": \"Serialization failed\" }";
        }
    }

    private void applySuccessStatus(Transaction transaction, Order order, LegacyPayOsCallbackRequest request) {
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setReferenceNumber(String.valueOf(request.getOrderCode()));
        transaction.setPaymentTime(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        log.info("Thanh toán thành công đơn hàng ID: {}. Hệ thống đã mở khóa khóa học.", order.getId());
    }

    private void applyFailedStatus(Transaction transaction, Order order, LegacyPayOsCallbackRequest request) {
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setReferenceNumber(String.valueOf(request.getOrderCode()));
        transaction.setPaymentTime(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCELLED);
        log.warn("Thanh toán thất bại hoặc bị hủy cho đơn hàng ID: {}", order.getId());
    }

    private void persistPaymentState(Transaction transaction, Order order) {
        transactionRepository.save(transaction);
        orderRepository.save(order);
    }

    private Optional<Transaction> findExistingTransaction(Order order) {
        return transactionRepository.findByOrderAndStatusIn(
                order, List.of(TransactionStatus.PROCESSING, TransactionStatus.SUCCESS));
    }
}
