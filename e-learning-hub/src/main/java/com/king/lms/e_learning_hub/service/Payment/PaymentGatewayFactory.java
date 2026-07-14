package com.king.lms.e_learning_hub.service.Payment;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final Map<String, PaymentGateway> gatewayMap;

    public PaymentGateway getGateway(String paymentMethod) {
        String service = "PaymentService";
        PaymentGateway gateway = gatewayMap.get(paymentMethod.toUpperCase()+service);
        
        if (gateway == null) {
            throw new IllegalArgumentException("Hệ thống chưa hỗ trợ phương thức: " + paymentMethod);
        }
        return gateway;
    }
}