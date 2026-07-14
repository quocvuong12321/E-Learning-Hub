package com.king.lms.e_learning_hub.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_order_code", columnList = "order_code"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_course_id", columnList = "course_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Column(name = "order_code", unique = true, nullable = false, length = 64)
    String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    OrderStatus status = OrderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    PaymentMethod paymentMethod;

    // Quan hệ một-nhiều với Transaction
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Transaction> transactions;

    /**
     * Enum cho trạng thái đơn hàng
     */
    public enum OrderStatus {
        PENDING,      // Chờ thanh toán
        COMPLETED,    // Thanh toán thành công
        CANCELLED     // Đã hủy
    }

    /**
     * Helper method: Kiểm tra đơn hàng đã thanh toán hay chưa
     */
    public Boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }

    public Boolean isCancelled(){
        return status == OrderStatus.CANCELLED;
    }
    /**
     * Helper method: Tính số tiền sau chiết khấu
     */
    public BigDecimal getFinalAmount() {
        if (discountAmount == null) {
            return totalAmount;
        }
        return totalAmount.subtract(discountAmount);
    }

    /**
     * Helper method: Lấy giao dịch thành công cuối cùng
     */
    public Transaction getSuccessfulTransaction() {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }
        return transactions.stream()
                .filter(txn -> txn.getStatus() == Transaction.TransactionStatus.SUCCESS)
                .findFirst()
                .orElse(null);
    }
}