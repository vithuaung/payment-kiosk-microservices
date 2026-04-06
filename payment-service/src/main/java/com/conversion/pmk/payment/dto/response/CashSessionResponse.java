package com.conversion.pmk.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashSessionResponse {

    private UUID cashId;
    private String sessionRef;
    private BigDecimal insertedAmt;
    private BigDecimal returnedAmt;
    private String sessionStatus;
}
