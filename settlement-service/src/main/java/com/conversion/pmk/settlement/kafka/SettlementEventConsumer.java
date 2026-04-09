package com.conversion.pmk.settlement.kafka;

import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleRequest;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.event.PaymentCompletedEvent;
import com.conversion.pmk.settlement.event.PaymentFailedEvent;
import com.conversion.pmk.settlement.event.PaymentInitiatedEvent;
import com.conversion.pmk.settlement.service.SettlementCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

// Consumes payment.initiated events and runs settlement in the background
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementEventConsumer {

    private final SettlementCoreService coreService;
    private final SettlementEventProducer producer;

    @KafkaListener(topics = "pmk.payment.initiated", groupId = "settlement-service-group")
    public void onPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received payment.initiated eventId={} sessionRef={}", event.getEventId(), event.getSessionRef());

        try {
            // Async path carries no bill items — SAP uses totalAmt from the event context
            SapSettleRequest sapRequest = SapSettleRequest.builder()
                    .sessionRef(event.getSessionRef())
                    .personRef(event.getPersonRef())
                    .payMethod(event.getPayMethod())
                    .items(Collections.emptyList())
                    .build();

            // Use real paymentId from event; fall back to derived UUID for backward-compat
            UUID paymentId = (event.getPaymentId() != null && !event.getPaymentId().isBlank())
                    ? UUID.fromString(event.getPaymentId())
                    : UUID.nameUUIDFromBytes(event.getSessionRef().getBytes());

            SettlementResponse result = coreService.doSettle(event.getSessionRef(), paymentId, sapRequest);

            if ("DONE".equals(result.getSettleStatus())) {
                producer.publishPaymentCompleted(PaymentCompletedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .paymentId(event.getPaymentId())
                        .sessionRef(result.getSessionRef())
                        .extRef(result.getExtRef())
                        .occurredAt(System.currentTimeMillis())
                        .build());
            } else if ("FAILED".equals(result.getSettleStatus())) {
                producer.publishPaymentFailed(PaymentFailedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .paymentId(event.getPaymentId())
                        .sessionRef(result.getSessionRef())
                        .failReason(result.getFailReason())
                        .retryCount(result.getRetryCount())
                        .occurredAt(System.currentTimeMillis())
                        .build());
            }

        } catch (Exception ex) {
            log.error("Settlement processing error sessionRef={} reason={}", event.getSessionRef(), ex.getMessage(), ex);
            producer.publishPaymentFailed(PaymentFailedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .paymentId(event.getPaymentId())
                    .sessionRef(event.getSessionRef())
                    .failReason(ex.getMessage())
                    .retryCount(0)
                    .occurredAt(System.currentTimeMillis())
                    .build());
        }
    }
}
