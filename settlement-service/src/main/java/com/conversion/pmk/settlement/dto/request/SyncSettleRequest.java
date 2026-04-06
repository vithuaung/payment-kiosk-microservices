package com.conversion.pmk.settlement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Request body for the synchronous settlement endpoint
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncSettleRequest {

    @NotBlank(message = "sessionRef is required")
    private String sessionRef;

    private UUID paymentId;

    private String personRef;

    private BigDecimal totalAmt;

    private String payMethod;

    private List<BillItemRef> billItems;

    // Reference to a single bill line
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillItemRef {
        private String billRef;
        private int billSeq;
        private BigDecimal payableAmt;
        private String orgCode;
    }
}
