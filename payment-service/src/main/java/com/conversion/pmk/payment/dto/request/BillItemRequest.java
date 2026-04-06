package com.conversion.pmk.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

    private String billRef;
    private int billSeq;
    private BigDecimal billedAmt;
    private BigDecimal payableAmt;
    private String orgCode;
    private String caseRef;
}
