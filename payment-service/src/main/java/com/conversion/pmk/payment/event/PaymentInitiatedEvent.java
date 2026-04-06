package com.conversion.pmk.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Kafka event published when a payment enters the PROCESSING state
@Data
@Builder
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
