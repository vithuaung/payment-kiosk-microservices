package com.conversion.pmk.notification.kafka;

import com.conversion.pmk.notification.event.PatientCheckinEvent;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Consumes patient check-in events and sends confirmation SMS
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientEventConsumer {

    // Placeholder — real system would resolve mobile from patient record
    private static final String PLACEHOLDER_MOBILE = "+6591234567";

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "pmk.patient.registered",
            groupId = "notification-service-group"
    )
    public void onPatientCheckin(PatientCheckinEvent event) {
        log.info("Received patient.registered eventId={} checkinId={}", event.getEventId(), event.getCheckinId());

        UUID checkinUuid = UUID.nameUUIDFromBytes(event.getCheckinId().getBytes());

        String body = String.format(
                "Check-in confirmed at %s. Your reference: %s",
                event.getLocationCode(), event.getCheckinId()
        );

        NotificationResponse result = notificationService.sendSms(checkinUuid, PLACEHOLDER_MOBILE, body);
        log.debug("Check-in SMS notifId={} status={}", result.getNotifId(), result.getSendStatus());
    }
}
