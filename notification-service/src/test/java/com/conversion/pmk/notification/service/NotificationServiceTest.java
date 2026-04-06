package com.conversion.pmk.notification.service;

import com.conversion.pmk.common.enums.NotificationChannel;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.entity.Notification;
import com.conversion.pmk.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    // Helper — return the same entity passed to save() with a generated ID
    private Notification stubSave(Notification n) {
        if (n.getNotifId() == null) {
            n = Notification.builder()
                    .notifId(UUID.randomUUID())
                    .paymentId(n.getPaymentId())
                    .notifChannel(n.getNotifChannel())
                    .recipient(n.getRecipient())
                    .subject(n.getSubject())
                    .bodyText(n.getBodyText())
                    .sendStatus(n.getSendStatus())
                    .sentAt(n.getSentAt())
                    .failReason(n.getFailReason())
                    .build();
        }
        return n;
    }

    @Test
    void sendEmail_success_savesWithSentStatus() {
        UUID paymentId = UUID.randomUUID();
        when(notificationRepository.save(any())).thenAnswer(inv -> stubSave(inv.getArgument(0)));
        when(emailService.send(any(), any(), any())).thenReturn(NotificationResult.success());

        NotificationResponse response = notificationService.sendEmail(
                paymentId, "test@sample.com", "Subject", "Body"
        );

        assertThat(response.getSendStatus()).isEqualTo("SENT");
        assertThat(response.getFailReason()).isNull();
        verify(notificationRepository, times(2)).save(any());
    }

    @Test
    void sendEmail_failure_savesWithFailedStatus() {
        UUID paymentId = UUID.randomUUID();
        when(notificationRepository.save(any())).thenAnswer(inv -> stubSave(inv.getArgument(0)));
        when(emailService.send(any(), any(), any()))
                .thenReturn(NotificationResult.failure("Mock SMTP error"));

        NotificationResponse response = notificationService.sendEmail(
                paymentId, "test@sample.com", "Subject", "Body"
        );

        assertThat(response.getSendStatus()).isEqualTo("FAILED");
        assertThat(response.getFailReason()).isEqualTo("Mock SMTP error");
    }

    @Test
    void sendSms_success_savesWithSentStatus() {
        UUID paymentId = UUID.randomUUID();
        when(notificationRepository.save(any())).thenAnswer(inv -> stubSave(inv.getArgument(0)));
        when(smsService.send(any(), any())).thenReturn(NotificationResult.success());

        NotificationResponse response = notificationService.sendSms(
                paymentId, "+6591234567", "Your reference: ABC"
        );

        assertThat(response.getSendStatus()).isEqualTo("SENT");
        assertThat(response.getNotifChannel()).isEqualTo(NotificationChannel.SMS.name());
    }

    @Test
    void getByPaymentId_returnsAllNotifications() {
        UUID paymentId = UUID.randomUUID();
        Notification n1 = Notification.builder()
                .notifId(UUID.randomUUID())
                .paymentId(paymentId)
                .notifChannel(NotificationChannel.EMAIL)
                .recipient("a@b.com")
                .sendStatus("SENT")
                .build();
        Notification n2 = Notification.builder()
                .notifId(UUID.randomUUID())
                .paymentId(paymentId)
                .notifChannel(NotificationChannel.SMS)
                .recipient("+6591234567")
                .sendStatus("SENT")
                .build();

        when(notificationRepository.findByPaymentId(paymentId)).thenReturn(List.of(n1, n2));

        List<NotificationResponse> responses = notificationService.getByPaymentId(paymentId);

        assertThat(responses).hasSize(2);
        verify(notificationRepository).findByPaymentId(paymentId);
    }
}
