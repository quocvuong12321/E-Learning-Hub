package com.king.lms.e_learning_hub.dto.payment;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.payos.model.webhooks.WebhookData;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayOsCallbackRequest {
    // @NotBlank(message = "Response code is required")
    String code; // "00"

    // @NotBlank(message = "Description is required")
    String desc; // "success"

    // @NotNull(message = "Success status is required")
    Boolean success; // true/false

    // @NotNull(message = "Webhook data payload is required")
    @Valid 
    WebhookData data;

    // @NotBlank(message = "Webhook signature is required")
    String signature;
}
