package com.conversion.pmk.settlement.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Incoming Kafka event from payment-service when a payment starts processing
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiatedEvent {

    private String eventId;
    private String sessionRef;
    private String personRef;
    private String terminalCode;
    private BigDecimal totalAmt;
    private String payMethod;
    private long occurredAt;
}
