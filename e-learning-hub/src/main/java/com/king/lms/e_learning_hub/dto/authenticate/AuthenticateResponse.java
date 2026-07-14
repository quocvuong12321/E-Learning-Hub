package com.king.lms.e_learning_hub.dto.authenticate;


import com.king.lms.e_learning_hub.dto.user.UserResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticateResponse {
    String accessToken;
    
    String refreshToken;
    
    String role;  // Cho local login
    
    boolean mustChangePassword;
    
    // ✅ Thêm cho OAuth login
    UserResponse user;  // ← User info
    
    String provider;  // ← OAuth provider (google, facebook, etc)
    
    String tokenType;  // "Bearer"
    
    Integer expiresIn;  // Seconds
}
