package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Entity
@Table(
    name = "payment_methods",
    indexes = {
        @Index(name = "idx_code", columnList = "code")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMethod extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    String name;

    @Column(name = "code", unique = true, nullable = false, length = 255)
    String code; // VD: MOMO, VNPAY, BANK_TRANSFER

    @Column(name = "description", length = 255)
    String description;

    @Column(name = "icon_url", length = 255)
    String iconUrl;

    // Quan hệ một-nhiều với Order (tuỳ chọn)
    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Order> orders;

    /**
     * Helper method: Kiểm tra đây có phải phương thức MOMO không
     */
    public Boolean isMomo() {
        return code != null && code.equalsIgnoreCase("MOMO");
    }

    /**
     * Helper method: Kiểm tra đây có phải phương thức VNPAY không
     */
    public Boolean isVnpay() {
        return code != null && code.equalsIgnoreCase("VNPAY");
    }

    /**
     * Helper method: Kiểm tra đây có phải chuyển khoản ngân hàng không
     */
    public Boolean isBankTransfer() {
        return code != null && code.equalsIgnoreCase("BANK_TRANSFER");
    }
}