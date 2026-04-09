package com.conversion.pmk.notification.kafka;

import com.conversion.pmk.notification.event.PaymentCompletedEvent;
import com.conversion.pmk.notification.event.PaymentFailedEvent;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Consumes payment outcome events and dispatches email/SMS notifications
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    // Placeholder — real system would resolve the customer's contact details from event context
    private static final String PLACEHOLDER_EMAIL  = "customer@sample.com";
    private static final String PLACEHOLDER_MOBILE = "+6591234567";

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "pmk.payment.completed",
            groupId = "notification-service-group",
            containerFactory = "completedEventListenerFactory"
    )
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment.completed eventId={} sessionRef={}", event.getEventId(), event.getSessionRef());

        UUID paymentId = (event.getPaymentId() != null && !event.getPaymentId().isBlank())
                ? UUID.fromString(event.getPaymentId())
                : UUID.nameUUIDFromBytes(event.getSessionRef().getBytes());

        String emailSubject = "Payment Confirmed";
        String emailBody = String.format(
                "Your payment %s has been processed. Reference: %s",
                event.getSessionRef(), event.getExtRef()
        );

        NotificationResponse emailResult = notificationService.sendEmail(
                paymentId, PLACEHOLDER_EMAIL, emailSubject, emailBody
        );
        log.debug("Email outcome notifId={} status={}", emailResult.getNotifId(), emailResult.getSendStatus());

        String smsBody = String.format("Payment confirmed. Ref: %s", event.getSessionRef());

        NotificationResponse smsResult = notificationService.sendSms(
                paymentId, PLACEHOLDER_MOBILE, smsBody
        );
        log.debug("SMS outcome notifId={} status={}", smsResult.getNotifId(), smsResult.getSendStatus());
    }

    @KafkaListener(
            topics = "pmk.payment.failed",
            groupId = "notification-service-group",
            containerFactory = "failedEventListenerFactory"
    )
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment.failed eventId={} sessionRef={}", event.getEventId(), event.getSessionRef());

        UUID paymentId = (event.getPaymentId() != null && !event.getPaymentId().isBlank())
                ? UUID.fromString(event.getPaymentId())
                : UUID.nameUUIDFromBytes(event.getSessionRef().getBytes());

        String subject = "Payment Failed";
        String body = String.format(
                "Payment %s failed after %d attempts. Reason: %s",
                event.getSessionRef(), event.getRetryCount(), event.getFailReason()
        );

        NotificationResponse result = notificationService.sendEmail(
                paymentId, PLACEHOLDER_EMAIL, subject, body
        );
        log.debug("Failure alert email notifId={} status={}", result.getNotifId(), result.getSendStatus());
    }
}
