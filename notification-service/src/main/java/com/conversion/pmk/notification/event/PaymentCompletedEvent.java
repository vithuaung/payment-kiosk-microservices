package com.conversion.pmk.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors the event published by settlement-service on pmk.payment.completed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    private String eventId;
    private String paymentId;
    private String sessionRef;
    private String extRef;
    private long occurredAt;
}
