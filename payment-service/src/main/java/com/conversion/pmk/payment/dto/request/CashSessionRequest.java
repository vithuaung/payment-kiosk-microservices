package com.conversion.pmk.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashSessionRequest {

    @NotBlank
    private String sessionRef;

    private BigDecimal insertedAmt;
    private BigDecimal returnedAmt;
    private String sessionStatus;
}
