package com.conversion.pmk.notification.kafka;

import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.event.PaymentCompletedEvent;
import com.conversion.pmk.notification.event.PaymentFailedEvent;
import com.conversion.pmk.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    // Stub a minimal response so the consumer's log call does not NPE
    private NotificationResponse stubResponse() {
        return NotificationResponse.builder()
                .notifId(UUID.randomUUID())
                .sendStatus("SENT")
                .build();
    }

    @Test
    void onPaymentCompleted_callsEmailAndSms() {
        when(notificationService.sendEmail(any(), any(), any(), any())).thenReturn(stubResponse());
        when(notificationService.sendSms(any(), any(), any())).thenReturn(stubResponse());

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID().toString(), "SESSION-001", "EXT-REF-001", System.currentTimeMillis()
        );

        paymentEventConsumer.onPaymentCompleted(event);

        verify(notificationService, times(1)).sendEmail(any(), any(), any(), any());
        verify(notificationService, times(1)).sendSms(any(), any(), any());
    }

    @Test
    void onPaymentFailed_callsEmailOnly() {
        when(notificationService.sendEmail(any(), any(), any(), any())).thenReturn(stubResponse());

        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID().toString(), "SESSION-002", "Timeout", 3, System.currentTimeMillis()
        );

        paymentEventConsumer.onPaymentFailed(event);

        verify(notificationService, times(1)).sendEmail(any(), any(), any(), any());
        verify(notificationService, never()).sendSms(any(), any(), any());
    }
}
