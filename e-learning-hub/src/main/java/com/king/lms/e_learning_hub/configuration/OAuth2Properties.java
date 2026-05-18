package com.king.lms.e_learning_hub.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "oauth2.google")
public class OAuth2Properties {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String userInfoUrl;
    private String redirectUri;
}