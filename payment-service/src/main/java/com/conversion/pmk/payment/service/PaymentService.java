package com.conversion.pmk.payment.service;

import com.conversion.pmk.payment.dto.request.CompletePaymentRequest;
import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse initiate(InitiatePaymentRequest request);

    PaymentResponse startProcessing(String sessionRef);

    PaymentResponse complete(CompletePaymentRequest request);

    PaymentResponse getBySessionRef(String sessionRef);
}
