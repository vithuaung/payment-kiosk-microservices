package com.conversion.pmk.mock.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// One bill line submitted for posting
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

    private String billRef;
    private int billSeq;
    private BigDecimal payableAmt;
}
