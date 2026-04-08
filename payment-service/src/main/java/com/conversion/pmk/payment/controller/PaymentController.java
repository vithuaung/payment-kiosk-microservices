package com.conversion.pmk.payment.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.payment.dto.request.CompletePaymentRequest;
import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import com.conversion.pmk.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> initiatePayment(
            @RequestBody @Valid InitiatePaymentRequest request) {
        return ApiResponse.ok(paymentService.initiate(request));
    }

    @GetMapping("/{sessionRef}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable("sessionRef") String sessionRef) {
        return ApiResponse.ok(paymentService.getBySessionRef(sessionRef));
    }

    @PutMapping("/{sessionRef}/start")
    public ApiResponse<PaymentResponse> startPayment(@PathVariable("sessionRef") String sessionRef) {
        return ApiResponse.ok(paymentService.startProcessing(sessionRef));
    }

    @PutMapping("/{sessionRef}/complete")
    public ApiResponse<PaymentResponse> completePayment(
            @PathVariable("sessionRef") String sessionRef,
            @RequestBody @Valid CompletePaymentRequest request) {
        return ApiResponse.ok(paymentService.complete(request));
    }
}
