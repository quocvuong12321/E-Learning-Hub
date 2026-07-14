package com.king.lms.e_learning_hub.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.payos.PayOS;

@Configuration
public class PayOsConfig {
    @Bean
    public PayOS payOs(PayOSProperties properties) {
        return new PayOS(
                properties.getClientId(),
                properties.getApiKey(),
                properties.getChecksumKey());
    }
}
