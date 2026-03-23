package com.king.lms.e_learning_hub.dto.authenticate;


import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessTokenResponse {
    String accessToken;
}
