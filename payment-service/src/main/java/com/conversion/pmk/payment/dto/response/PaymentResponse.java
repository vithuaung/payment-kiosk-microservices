package com.conversion.pmk.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID paymentId;
    private String sessionRef;
    private String personRef;
    private String terminalCode;
    private BigDecimal totalAmt;
    private BigDecimal paidAmt;
    private BigDecimal changeAmt;
    private String payMethod;
    private String payStatus;
    private String startedAt;
    private String finishedAt;
    private List<BillItemResponse> billItems;
}
