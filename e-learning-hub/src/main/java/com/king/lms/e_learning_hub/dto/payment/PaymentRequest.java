package com.king.lms.e_learning_hub.dto.payment;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    @NotNull(message = "Course id is required")
    Long courseId;
    @NotNull(message = "Code Payment Method is required")
    String codePaymentMethod;
}
