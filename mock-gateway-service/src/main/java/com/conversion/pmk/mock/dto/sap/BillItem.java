package com.conversion.pmk.mock.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Single bill line returned from the mock SAP billing system
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {

    private String billRef;
    private int billSeq;
    private String billDesc;
    private BigDecimal billAmt;
    private BigDecimal payableAmt;
    private String billDate;
    private String billStatus;
}
