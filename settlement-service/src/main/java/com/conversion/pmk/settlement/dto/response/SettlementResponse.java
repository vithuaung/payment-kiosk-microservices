package com.conversion.pmk.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// Returned by both the sync endpoint and query endpoints
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {

    private UUID settlementId;
    private String sessionRef;
    private String settleStatus;
    private String extRef;
    private int retryCount;
    private String settledAt;
    private String failReason;
    private List<AttemptResponse> attempts;
}
