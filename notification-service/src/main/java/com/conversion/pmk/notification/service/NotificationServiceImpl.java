package com.conversion.pmk.notification.service;

import com.conversion.pmk.common.enums.NotificationChannel;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.entity.Notification;
import com.conversion.pmk.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SENT    = "SENT";
    private static final String STATUS_FAILED  = "FAILED";

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    public NotificationResponse sendEmail(UUID paymentId, String recipient, String subject, String body) {
        // Save initial record so we have a row even if the send crashes
        Notification notification = Notification.builder()
                .paymentId(paymentId)
                .notifChannel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject(subject)
                .bodyText(body)
                .sendStatus(STATUS_PENDING)
                .build();
        notification = notificationRepository.save(notification);

        NotificationResult result = emailService.send(recipient, subject, body);

        if (result.isSent()) {
            notification.setSendStatus(STATUS_SENT);
            notification.setSentAt(LocalDateTime.now());
            log.debug("Email sent notifId={} recipient={}", notification.getNotifId(), recipient);
        } else {
            notification.setSendStatus(STATUS_FAILED);
            notification.setFailReason(result.getErrorMessage());
            log.warn("Email failed notifId={} reason={}", notification.getNotifId(), result.getErrorMessage());
        }

        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Override
    public NotificationResponse sendSms(UUID paymentId, String recipient, String body) {
        Notification notification = Notification.builder()
                .paymentId(paymentId)
                .notifChannel(NotificationChannel.SMS)
                .recipient(recipient)
                .bodyText(body)
                .sendStatus(STATUS_PENDING)
                .build();
        notification = notificationRepository.save(notification);

        NotificationResult result = smsService.send(recipient, body);

        if (result.isSent()) {
            notification.setSendStatus(STATUS_SENT);
            notification.setSentAt(LocalDateTime.now());
            log.debug("SMS sent notifId={} recipient={}", notification.getNotifId(), recipient);
        } else {
            notification.setSendStatus(STATUS_FAILED);
            notification.setFailReason(result.getErrorMessage());
            log.warn("SMS failed notifId={} reason={}", notification.getNotifId(), result.getErrorMessage());
        }

        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Override
    public List<NotificationResponse> getByPaymentId(UUID paymentId) {
        return notificationRepository.findByPaymentId(paymentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Manual mapping — avoids MapStruct dependency for this simple conversion
    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notifId(n.getNotifId())
                .paymentId(n.getPaymentId() != null ? n.getPaymentId().toString() : null)
                .notifChannel(n.getNotifChannel() != null ? n.getNotifChannel().name() : null)
                .recipient(n.getRecipient())
                .subject(n.getSubject())
                .sendStatus(n.getSendStatus())
                .sentAt(n.getSentAt() != null ? n.getSentAt().toString() : null)
                .failReason(n.getFailReason())
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                .build();
    }
}
