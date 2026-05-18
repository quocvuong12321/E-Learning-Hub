package com.king.lms.e_learning_hub.dto.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dùng cho frontend gửi authorization code sau khi nhận redirect từ Google
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthCallbackRequest {
    
    private String code;                    // Authorization code từ Google redirect
    
    private String state;                   // CSRF protection token
    
    private String scope;                   // Approved scopes
    
    private String authuser;                // Google account index (optional)
    
    private String prompt;                  // Prompt parameter (optional)
}