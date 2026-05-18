package com.king.lms.e_learning_hub.dto.oauth2;

import com.king.lms.e_learning_hub.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginResponse {
    
    private String accessToken;             // JWT access token
    
    private String refreshToken;            // JWT refresh token
    
    private UserResponse user;              // User information
    
    private String provider;                // OAuth provider: "google", "facebook", etc
    
    private String tokenType;               // Token type: "Bearer"
    
    private Long expiresIn;                 // Access token expiration in seconds
}