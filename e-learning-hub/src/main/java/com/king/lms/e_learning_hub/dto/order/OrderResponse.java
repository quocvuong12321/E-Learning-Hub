package com.king.lms.e_learning_hub.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.auto.value.AutoValue.Builder;
import com.king.lms.e_learning_hub.dto.course.CourseResponse;
import com.king.lms.e_learning_hub.dto.payment.PaymentResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    String orderCode;
    Long userId;
    CourseResponse course;
    BigDecimal totalAmount;
    BigDecimal discountAmount;
    BigDecimal finalAmount; // Helper field: tính toán từ totalAmount - discountAmount
    String status;
    String paymentMethod;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
