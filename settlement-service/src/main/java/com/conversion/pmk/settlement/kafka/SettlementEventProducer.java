package com.conversion.pmk.settlement.kafka;

import com.conversion.pmk.settlement.event.PaymentCompletedEvent;
import com.conversion.pmk.settlement.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Publishes settlement outcome events to Kafka
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementEventProducer {

    private static final String TOPIC_COMPLETED = "pmk.payment.completed";
    private static final String TOPIC_FAILED    = "pmk.payment.failed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        log.debug("Publishing payment.completed sessionRef={}", event.getSessionRef());
        kafkaTemplate.send(TOPIC_COMPLETED, event.getSessionRef(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        log.debug("Publishing payment.failed sessionRef={}", event.getSessionRef());
        kafkaTemplate.send(TOPIC_FAILED, event.getSessionRef(), event);
    }
}
