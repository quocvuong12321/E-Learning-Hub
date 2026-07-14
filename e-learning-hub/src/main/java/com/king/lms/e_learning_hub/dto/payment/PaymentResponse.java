package com.king.lms.e_learning_hub.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String checkoutUrl;  // Đường link dẫn tới trang quét mã QR / Thanh toán
    private Long orderCode;
    private String transactionCode; // mà transaction cần update sau này 
}
