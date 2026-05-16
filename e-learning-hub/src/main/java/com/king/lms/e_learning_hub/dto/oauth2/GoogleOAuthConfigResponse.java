package com.king.lms.e_learning_hub.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

// GoogleOAuthConfigResponse.java
@Data
@Builder
public class GoogleOAuthConfigResponse {
    @JsonProperty("authorization_url")
    private String authorizationUrl;
}