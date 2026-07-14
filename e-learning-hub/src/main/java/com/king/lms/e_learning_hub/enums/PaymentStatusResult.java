package com.king.lms.e_learning_hub.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusResult {
    SUCCESS("Thanh toán thành công"),
    FAILED("Thanh toán thất bại hoặc bị hủy"),
    ALREADY_PROCESSED("Giao dịch đã được xử lý trước đó");

    private final String message;

    PaymentStatusResult(String message) {
        this.message = message;
    }
}