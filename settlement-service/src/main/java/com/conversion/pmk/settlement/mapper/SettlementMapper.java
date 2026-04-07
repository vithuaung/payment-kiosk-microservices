package com.conversion.pmk.settlement.mapper;

import com.conversion.pmk.settlement.dto.response.AttemptResponse;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.entity.Settlement;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Static mapping from Settlement entity to SettlementResponse DTO
public class SettlementMapper {

    private SettlementMapper() {}

    public static SettlementResponse toResponse(Settlement s) {
        List<AttemptResponse> attempts = (s.getAttempts() == null)
                ? Collections.emptyList()
                : s.getAttempts().stream()
                        .map(a -> AttemptResponse.builder()
                                .attemptId(a.getAttemptId())
                                .attemptNo(a.getAttemptNo())
                                .resultStatus(a.getResultStatus())
                                .attemptedAt(a.getAttemptedAt() != null ? a.getAttemptedAt().toString() : null)
                                .build())
                        .collect(Collectors.toList());

        return SettlementResponse.builder()
                .settlementId(s.getSettlementId())
                .sessionRef(s.getSessionRef())
                .settleStatus(s.getSettleStatus() != null ? s.getSettleStatus().name() : null)
                .extRef(s.getExtRef())
                .retryCount(s.getRetryCount())
                .settledAt(s.getSettledAt() != null ? s.getSettledAt().toString() : null)
                .failReason(s.getFailReason())
                .attempts(attempts)
                .build();
    }
}
