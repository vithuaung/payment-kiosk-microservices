package com.conversion.pmk.settlement.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Outgoing Kafka event when settlement fails or max retries are exhausted
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {

    private String eventId;
    private String sessionRef;
    private String failReason;
    private int retryCount;
    private long occurredAt;
}
