package com.conversion.pmk.settlement.service;

import com.conversion.pmk.common.enums.SettlementStatus;
import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleRequest;
import com.conversion.pmk.settlement.dto.request.SyncSettleRequest;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.mapper.SettlementMapper;
import com.conversion.pmk.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// Entry point for the synchronous HTTP settlement path
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncSettlementService {

    private final SettlementCoreService coreService;
    private final SettlementRepository settlementRepository;

    // synchronized prevents two threads from settling the same payment concurrently
    @Transactional
    public synchronized SettlementResponse settle(SyncSettleRequest request) {
        log.debug("Sync settle request sessionRef={}", request.getSessionRef());

        // Guard: if another thread already pushed the session to DONE, return it
        Optional<SettlementResponse> earlyReturn = settlementRepository
                .findBySessionRefAndSettleStatus(request.getSessionRef(), SettlementStatus.DONE)
                .map(SettlementMapper::toResponse);

        if (earlyReturn.isPresent()) {
            log.debug("Sync settle short-circuit — already DONE sessionRef={}", request.getSessionRef());
            return earlyReturn.get();
        }

        // Build SAP request from the incoming HTTP request
        List<SapSettleRequest.BillItemRef> items = request.getBillItems() == null
                ? Collections.emptyList()
                : request.getBillItems().stream()
                        .map(b -> SapSettleRequest.BillItemRef.builder()
                                .billRef(b.getBillRef())
                                .billSeq(b.getBillSeq())
                                .payableAmt(b.getPayableAmt())
                                .orgCode(b.getOrgCode())
                                .build())
                        .collect(Collectors.toList());

        SapSettleRequest sapRequest = SapSettleRequest.builder()
                .sessionRef(request.getSessionRef())
                .personRef(request.getPersonRef())
                .payMethod(request.getPayMethod())
                .items(items)
                .build();

        return coreService.doSettle(request.getSessionRef(), request.getPaymentId(), sapRequest);
    }
}
