package com.king.lms.e_learning_hub.dto.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthErrorResponse {
    
    private String error;                   // Error code
    
    private String errorDescription;        // Error description
    
    private String errorUri;                // Error URI (optional)
    
    private String state;                   // State parameter for CSRF validation
}