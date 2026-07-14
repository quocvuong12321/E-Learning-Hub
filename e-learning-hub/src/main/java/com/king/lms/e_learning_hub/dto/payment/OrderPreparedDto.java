package com.king.lms.e_learning_hub.dto.payment;

import java.math.BigDecimal;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

//Dùng để tạo body requst gửi đến cổng thanh toán
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPreparedDto {
    Long orderId;
    String courseTitle;
    BigDecimal totalAmount;
    String transactionCode;
    
}
