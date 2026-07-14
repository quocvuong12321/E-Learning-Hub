package com.king.lms.e_learning_hub.service.Payment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.king.lms.e_learning_hub.entity.Transaction;
import com.king.lms.e_learning_hub.entity.Transaction.TransactionStatus;
import com.king.lms.e_learning_hub.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupJob {

    private final TransactionRepository transactionRepository;
    private final TransactionCleanupProcessor cleanupProcessor;
    @Value("${payment.cleanup.threshold-hours:24}")
    private int thresholdHours;

    @Value("${payment.cleanup.interval-ms:3600000}")
    private long intervalMs;

    // Run periodically (default every hour) to expire stale PROCESSING transactions
    @Scheduled(cron = "${payment.cleanup.cron:0 0 2 * * ?}")
    public void expireStaleProcessingTransactions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(thresholdHours);
        
        // Chỉ ĐỌC danh sách bản ghi kẹt (Không mở Transaction lớn ghi dữ liệu)
        List<Transaction> staleTransactions = transactionRepository.findByStatusAndCreatedAtBefore(
                TransactionStatus.PROCESSING, cutoff
        );

        if (staleTransactions == null || staleTransactions.isEmpty()) {
            return;
        }

        log.info("Hệ thống phát hiện {} giao dịch PROCESSING bị kẹt vượt quá {} giờ. Tiến hành dọn dẹp...", 
                staleTransactions.size(), thresholdHours);

        int successCount = 0;
        for (Transaction tx : staleTransactions) {
            try {
                // Gọi lớp xử lý với Transaction biệt lập cho từng Đơn hàng
                cleanupProcessor.expireSingleTransactionAndOrder(tx, thresholdHours);
                successCount++;
            } catch (Exception ex) {
                // Nếu bản ghi này lỗi, transaction của riêng nó giải phóng, vòng lặp vẫn tiếp tục dọn dẹp bản ghi tiếp theo
                log.error("Dọn dẹp thất bại cho Giao dịch ID={}, Mã số={}", tx.getId(), tx.getTransactionCode(), ex);
            }
        }
        
        log.info("Hoàn tất dọn dẹp hệ thống: Thành công {}/{} bản ghi.", successCount, staleTransactions.size());
    }
}
