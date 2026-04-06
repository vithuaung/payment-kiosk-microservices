package com.conversion.pmk.patient.kafka;

import com.conversion.pmk.patient.event.PatientCheckinEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Publishes patient check-in events to Kafka
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientEventProducer {

    private static final String TOPIC = "pmk.patient.registered";

    private final KafkaTemplate<String, PatientCheckinEvent> kafkaTemplate;

    // Sends a check-in event; key is personRef for partition affinity
    public void publishCheckin(PatientCheckinEvent event) {
        log.debug("Publishing checkin event for personRef={} to topic={}", event.getPersonRef(), TOPIC);
        kafkaTemplate.send(TOPIC, event.getPersonRef(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish checkin event for personRef={}: {}", event.getPersonRef(), ex.getMessage());
                    } else {
                        log.debug("Checkin event published: offset={}", result.getRecordMetadata().offset());
                    }
                });
    }
}
