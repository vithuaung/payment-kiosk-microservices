package com.conversion.pmk.settlement.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Outgoing Kafka event when settlement finishes successfully
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    private String eventId;
    private String paymentId;
    private String sessionRef;
    private String extRef;
    private long occurredAt;
}
