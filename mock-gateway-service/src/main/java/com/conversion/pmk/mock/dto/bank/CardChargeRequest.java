package com.conversion.pmk.mock.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardChargeRequest {

    private String sessionRef;
    private BigDecimal amount;
    private String cardNetwork;
    private String terminalCode;
}
