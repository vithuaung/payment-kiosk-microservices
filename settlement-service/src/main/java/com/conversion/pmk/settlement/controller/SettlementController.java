package com.conversion.pmk.settlement.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.settlement.dto.request.RetrySettleRequest;
import com.conversion.pmk.settlement.dto.request.SyncSettleRequest;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.entity.Settlement;
import com.conversion.pmk.settlement.repository.SettlementRepository;
import com.conversion.pmk.settlement.service.SettlementCoreService;
import com.conversion.pmk.settlement.service.SyncSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.conversion.pmk.settlement.dto.response.AttemptResponse;

// REST endpoints for settlement — sync settle, lookup, and manual retry
@Tag(name = "Settlement", description = "Settlement management endpoints")
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SyncSettlementService syncSettlementService;
    private final SettlementRepository settlementRepository;

    // Synchronous settle — caller blocks until SAP responds
    @Operation(summary = "Settle a payment synchronously")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<SettlementResponse>> syncSettle(
            @Valid @RequestBody SyncSettleRequest request) {

        SettlementResponse result = syncSettlementService.settle(request);
        return ResponseEntity.ok(ApiResponse.ok("Settlement processed", result));
    }

    // Retrieve settlement record by session reference
    @Operation(summary = "Get settlement by sessionRef")
    @GetMapping("/{sessionRef}")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlement(
            @PathVariable String sessionRef) {

        Settlement settlement = settlementRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", sessionRef));

        return ResponseEntity.ok(ApiResponse.ok(toResponse(settlement)));
    }

    // Manually trigger a retry for a failed settlement
    @Operation(summary = "Retry a failed settlement")
    @PostMapping("/{sessionRef}/retry")
    public ResponseEntity<ApiResponse<SettlementResponse>> retrySettle(
            @PathVariable String sessionRef,
            @Valid @RequestBody RetrySettleRequest request) {

        // Re-use the sync path — it will reload the existing settlement and re-attempt
        SyncSettleRequest syncRequest = SyncSettleRequest.builder()
                .sessionRef(sessionRef)
                .build();

        SettlementResponse result = syncSettlementService.settle(syncRequest);
        return ResponseEntity.ok(ApiResponse.ok("Retry processed", result));
    }

    // Maps entity to response DTO for the query endpoint
    private SettlementResponse toResponse(Settlement s) {
        List<AttemptResponse> attemptResponses = (s.getAttempts() == null)
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
                .attempts(attemptResponses)
                .build();
    }
}
