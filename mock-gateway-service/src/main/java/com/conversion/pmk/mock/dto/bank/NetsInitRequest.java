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
public class NetsInitRequest {

    private String sessionRef;
    private BigDecimal amount;
    private String terminalCode;
}
