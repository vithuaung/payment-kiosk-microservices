package com.conversion.pmk.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Single attempt summary included in SettlementResponse
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {

    private UUID attemptId;
    private int attemptNo;
    private String resultStatus;
    private String attemptedAt;
}
