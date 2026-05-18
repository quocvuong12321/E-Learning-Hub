package com.king.lms.e_learning_hub.dto.oauth2;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    
    private String sub;                     // Unique user identifier
    
    private String email;
    
    private String name;
    
    @SerializedName("given_name")
    private String givenName;
    
    @SerializedName("family_name")
    private String familyName;
    
    private String picture;
    
    @SerializedName("email_verified")
    private Boolean emailVerified;
    
    private String locale;
    
    @SerializedName("aud")
    private String audience;                // Token audience (client ID)
    
    @SerializedName("iss")
    private String issuer;                  // Token issuer
    
    @SerializedName("iat")
    private Long issuedAt;                  // Issued at
    
    @SerializedName("exp")
    private Long expiresAt;                 // Expires at
}