package com.conversion.pmk.payment.kafka;

import com.conversion.pmk.payment.event.PaymentInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Publishes payment domain events to Kafka topics
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String PAYMENT_INITIATED_TOPIC = "pmk.payment.initiated";

    private final KafkaTemplate<String, PaymentInitiatedEvent> kafkaTemplate;

    public void publishPaymentInitiated(PaymentInitiatedEvent event) {
        kafkaTemplate.send(PAYMENT_INITIATED_TOPIC, event.getSessionRef(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Published PaymentInitiatedEvent for sessionRef={} to topic={} partition={} offset={}",
                                event.getSessionRef(),
                                PAYMENT_INITIATED_TOPIC,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish PaymentInitiatedEvent for sessionRef={}: {}",
                                event.getSessionRef(), ex.getMessage(), ex);
                    }
                });
    }
}
