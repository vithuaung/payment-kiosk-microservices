package com.conversion.pmk.payment.controller;

import com.conversion.pmk.common.exception.GlobalExceptionHandler;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.payment.dto.request.CompletePaymentRequest;
import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import com.conversion.pmk.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    void initiatePayment_validRequest_returns200() throws Exception {
        InitiatePaymentRequest request = InitiatePaymentRequest.builder()
                .personRef("P-001")
                .terminalCode("TERM-001")
                .payMethod("CASH")
                .totalAmt(BigDecimal.valueOf(100.00))
                .billItems(Collections.emptyList())
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef("sess-abc-123")
                .personRef("P-001")
                .terminalCode("TERM-001")
                .payStatus("PENDING")
                .totalAmt(BigDecimal.valueOf(100.00))
                .build();

        when(paymentService.initiate(any(InitiatePaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionRef").value("sess-abc-123"))
                .andExpect(jsonPath("$.data.payStatus").value("PENDING"));
    }

    @Test
    void getPayment_existingSession_returns200() throws Exception {
        String sessionRef = "sess-abc-123";

        PaymentResponse response = PaymentResponse.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .personRef("P-001")
                .payStatus("PENDING")
                .totalAmt(BigDecimal.valueOf(100.00))
                .build();

        when(paymentService.getBySessionRef(sessionRef)).thenReturn(response);

        mockMvc.perform(get("/api/payments/{sessionRef}", sessionRef))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionRef").value(sessionRef));
    }

    @Test
    void completePayment_validRequest_returns200() throws Exception {
        String sessionRef = "sess-abc-123";

        CompletePaymentRequest request = CompletePaymentRequest.builder()
                .sessionRef(sessionRef)
                .paidAmt(BigDecimal.valueOf(100.00))
                .changeAmt(BigDecimal.ZERO)
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .payStatus("DONE")
                .paidAmt(BigDecimal.valueOf(100.00))
                .changeAmt(BigDecimal.ZERO)
                .build();

        when(paymentService.complete(any(CompletePaymentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/payments/{sessionRef}/complete", sessionRef)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.payStatus").value("DONE"));
    }

    @Test
    void initiatePayment_missingPersonRef_returns400() throws Exception {
        // personRef is @NotBlank — omitting it should trigger validation failure
        InitiatePaymentRequest request = InitiatePaymentRequest.builder()
                .personRef("")   // blank -> constraint violation
                .terminalCode("TERM-001")
                .payMethod("CASH")
                .totalAmt(BigDecimal.valueOf(100.00))
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
