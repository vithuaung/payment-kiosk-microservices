package com.conversion.pmk.settlement.service;

import com.conversion.pmk.common.enums.SettlementStatus;
import com.conversion.pmk.common.exception.PaymentException;
import com.conversion.pmk.settlement.client.SapSettleClient;
import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleRequest;
import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleResponse;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.entity.SettleAttempt;
import com.conversion.pmk.settlement.entity.Settlement;
import com.conversion.pmk.settlement.repository.SettleAttemptRepository;
import com.conversion.pmk.settlement.repository.SettlementRepository;
import com.conversion.pmk.settlement.mapper.SettlementMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// Shared core logic used by both the sync HTTP path and the async Kafka path
@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementCoreService {

    private final SettlementRepository settlementRepository;
    private final SettleAttemptRepository attemptRepository;
    private final SapSettleClient sapSettleClient;
    private final ObjectMapper objectMapper;

    @Value("${pmk.settlement.max-retry:5}")
    private int configuredMaxRetry;

    @Transactional
    public SettlementResponse doSettle(String sessionRef, UUID paymentId, SapSettleRequest sapRequest) {

        // 1. Check for existing settlement and handle idempotency / guard states
        Optional<Settlement> existing = settlementRepository.findBySessionRef(sessionRef);

        if (existing.isPresent()) {
            Settlement s = existing.get();

            if (s.getSettleStatus() == SettlementStatus.DONE) {
                // Already settled — return as-is (idempotent)
                log.debug("Settlement already DONE for sessionRef={}", sessionRef);
                return SettlementMapper.toResponse(s);
            }

            if (s.getSettleStatus() == SettlementStatus.FAILED && s.getRetryCount() >= s.getMaxRetry()) {
                throw new PaymentException("Max retries reached for sessionRef=" + sessionRef, "SETTLE_MAX_RETRY");
            }

            if (s.getSettleStatus() == SettlementStatus.PROCESSING) {
                throw new PaymentException("Settlement in progress for sessionRef=" + sessionRef, "SETTLE_IN_PROGRESS");
            }
        }

        // 2. Create new or reload existing settlement; mark as PROCESSING
        Settlement settlement = existing.orElseGet(() -> Settlement.builder()
                .paymentId(paymentId)
                .sessionRef(sessionRef)
                .personRef(sapRequest.getPersonRef())
                .payMethod(sapRequest.getPayMethod())
                .maxRetry(configuredMaxRetry)
                .retryCount(0)
                .build());

        settlement.setSettleStatus(SettlementStatus.PROCESSING);
        settlement = settlementRepository.save(settlement);

        // 3. Record the outgoing attempt
        int attemptNo = settlement.getRetryCount() + 1;
        String sentJson = toJson(sapRequest);

        SettleAttempt attempt = SettleAttempt.builder()
                .settlement(settlement)
                .attemptNo(attemptNo)
                .attemptedAt(LocalDateTime.now())
                .build();

        // 4. Call SAP
        try {
            SapSettleResponse sapResponse = sapSettleClient.settle(sapRequest);
            // Success path
            attempt.setResultStatus("OK");
            attempt.setSentData(sentJson);
            attempt.setRecvData(toJson(sapResponse));

            settlement.setExtRef(sapResponse.getExtRef());
            settlement.setSettleStatus(SettlementStatus.DONE);
            settlement.setSettledAt(LocalDateTime.now());
            settlement.setFailReason(null);

            log.info("Settlement DONE sessionRef={} extRef={}", sessionRef, sapResponse.getExtRef());

        } catch (Exception ex) {
            // Failure path
            attempt.setResultStatus("FAILED");
            attempt.setSentData(sentJson);
            attempt.setRecvData(ex.getMessage());

            settlement.setRetryCount(settlement.getRetryCount() + 1);
            settlement.setFailReason(ex.getMessage());

            if (settlement.getRetryCount() >= settlement.getMaxRetry()) {
                settlement.setSettleStatus(SettlementStatus.FAILED);
                log.warn("Settlement FAILED (max retry) sessionRef={}", sessionRef);
            } else {
                settlement.setSettleStatus(SettlementStatus.PENDING);
                log.warn("Settlement attempt failed, retryCount={} sessionRef={}", settlement.getRetryCount(), sessionRef);
            }
        }

        // 5. Persist
        settlement.setLastAttemptAt(LocalDateTime.now());
        attempt.setSettlement(settlement);
        settlement = settlementRepository.save(settlement);
        attemptRepository.save(attempt);

        // 6. Map and return
        return SettlementMapper.toResponse(settlement);
    }

    // Serialises an object to JSON; returns raw message on error
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            return obj != null ? obj.toString() : "null";
        }
    }
}
