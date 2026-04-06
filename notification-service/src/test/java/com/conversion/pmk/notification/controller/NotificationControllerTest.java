package com.conversion.pmk.notification.controller;

import com.conversion.pmk.common.exception.GlobalExceptionHandler;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(GlobalExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getByPaymentId_returns200WithList() throws Exception {
        UUID paymentId = UUID.randomUUID();

        NotificationResponse n = NotificationResponse.builder()
                .notifId(UUID.randomUUID())
                .paymentId(paymentId.toString())
                .notifChannel("EMAIL")
                .recipient("customer@sample.com")
                .subject("Payment Confirmed")
                .sendStatus("SENT")
                .build();

        when(notificationService.getByPaymentId(paymentId)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/payment/{paymentId}", paymentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].sendStatus").value("SENT"))
                .andExpect(jsonPath("$.data[0].notifChannel").value("EMAIL"));
    }

    @Test
    void getByPaymentId_emptyResult_returns200WithEmptyList() throws Exception {
        UUID paymentId = UUID.randomUUID();

        when(notificationService.getByPaymentId(paymentId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications/payment/{paymentId}", paymentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
