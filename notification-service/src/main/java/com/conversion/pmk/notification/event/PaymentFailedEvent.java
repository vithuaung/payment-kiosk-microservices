package com.conversion.pmk.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors the event published by settlement-service on pmk.payment.failed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {

    private String eventId;
    private String sessionRef;
    private String failReason;
    private int retryCount;
    private long occurredAt;
}
