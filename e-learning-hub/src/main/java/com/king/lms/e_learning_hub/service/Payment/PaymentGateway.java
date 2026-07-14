package com.king.lms.e_learning_hub.service.Payment;


import com.king.lms.e_learning_hub.dto.payment.PayOsCallbackRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentRequest;
import com.king.lms.e_learning_hub.dto.payment.PaymentResponse;
import com.king.lms.e_learning_hub.enums.PaymentStatusResult;

import vn.payos.exception.APIException;

public interface PaymentGateway {

    PaymentResponse createPaymentLink(PaymentRequest request) throws APIException;

    String checkPaymentStatus(Long orderCode) throws APIException;

    PaymentStatusResult updatePaymentStatus(PayOsCallbackRequest request);

}
