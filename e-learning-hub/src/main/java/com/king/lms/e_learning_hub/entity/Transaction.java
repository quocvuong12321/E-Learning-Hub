package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_txn_code", columnList = "transaction_code"),
        @Index(name = "idx_ref_number", columnList = "reference_number"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(name = "transaction_code", unique = true, nullable = false, length = 64)
    String transactionCode;

    @Column(name = "reference_number", length = 255)
    String referenceNumber; // Mã TxnRef từ VNPAY, MOMO, ngân hàng

    @Column(name = "payment_method", nullable = false, length = 50)
    String paymentMethod; // MOMO, VNPAY, BANK_TRANSFER, v.v.

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    TransactionStatus status = TransactionStatus.PROCESSING;

    @Column(name = "payment_time")
    LocalDateTime paymentTime; // Thời gian ngân hàng xác nhận thành công

    @Column(name = "raw_response", columnDefinition = "LONGTEXT")
    String rawResponse; // JSON/Webhook từ bên thứ 3

    /**
     * Enum cho trạng thái giao dịch
     */
    public enum TransactionStatus {
        PROCESSING,  // Đang xử lý
        SUCCESS,     // Thành công
        FAILED,       // Thất bại
    }

    /**
     * Helper method: Kiểm tra giao dịch đã thành công
     */
    public Boolean isSuccess() {
        return status == TransactionStatus.SUCCESS;
    }

    /**
     * Helper method: Kiểm tra giao dịch đã xử lý xong (SUCCESS hoặc FAILED)
     */
    public Boolean isProcessed() {
        return status != TransactionStatus.PROCESSING;
    }

    /**
     * Helper method: Lấy thời gian chờ xử lý (từ lúc tạo đến khi xác nhận)
     */
    public Long getProcessingTimeInSeconds() {
        if (paymentTime == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.SECONDS.between(getCreatedAt(), paymentTime);
    }
}