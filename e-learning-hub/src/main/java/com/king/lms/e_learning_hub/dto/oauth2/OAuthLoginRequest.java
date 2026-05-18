package com.king.lms.e_learning_hub.dto.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequest {
    
    private String code;                    // Authorization code từ Google OAuth
    
    private String state;                   // CSRF protection token (optional)
    
    private String idToken;                 // Alternative: Direct ID token (optional)
}