package com.king.lms.e_learning_hub.service.Payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.entity.Transaction;
import com.king.lms.e_learning_hub.entity.Order.OrderStatus;
import com.king.lms.e_learning_hub.entity.Transaction.TransactionStatus;
import com.king.lms.e_learning_hub.repository.OrderRepository;
import com.king.lms.e_learning_hub.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class TransactionCleanupProcessor {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    // REQUIRES_NEW: Mỗi một đơn hàng được xử lý trong một Transaction độc lập hoàn toàn
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void expireSingleTransactionAndOrder(Transaction tx, int thresholdHours) {
        // 1. Cập nhật trạng thái Giao dịch hệ thống
        tx.setStatus(TransactionStatus.FAILED);
        tx.setRawResponse("Expired by automated background cleanup job after " + thresholdHours + " hours.");
        tx.setPaymentTime(LocalDateTime.now());
        transactionRepository.save(tx); // Đẩy vào persistence context

        // 2. Cập nhật trạng thái Đơn hàng đính kèm sang CANCELLED để học viên có thể bấm thanh toán lại
        if (tx.getOrder() != null) {
            var order = tx.getOrder();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        
        log.debug("Đã dọn dẹp thành công đơn kẹt: Tx ID = {}, Order ID = {}", tx.getId(), tx.getOrder().getId());
    }
}