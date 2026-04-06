package com.conversion.pmk.settlement.service;

import com.conversion.pmk.common.enums.SettlementStatus;
import com.conversion.pmk.settlement.dto.request.SyncSettleRequest;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.entity.Settlement;
import com.conversion.pmk.settlement.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncSettlementServiceTest {

    @Mock private SettlementCoreService coreService;
    @Mock private SettlementRepository settlementRepository;

    @InjectMocks
    private SyncSettlementService syncService;

    @Test
    void settle_success_returnsDoneStatus() {
        SyncSettleRequest request = SyncSettleRequest.builder()
                .sessionRef("sess-100")
                .paymentId(UUID.randomUUID())
                .personRef("P-100")
                .payMethod("CASH")
                .billItems(Collections.emptyList())
                .build();

        // No existing DONE record yet
        when(settlementRepository.findBySessionRefAndSettleStatus(eq("sess-100"), eq(SettlementStatus.DONE)))
                .thenReturn(Optional.empty());

        SettlementResponse expected = SettlementResponse.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-100")
                .settleStatus("DONE")
                .extRef("EXT-100")
                .build();
        when(coreService.doSettle(anyString(), any(UUID.class), any())).thenReturn(expected);

        SettlementResponse result = syncService.settle(request);

        assertThat(result.getSettleStatus()).isEqualTo("DONE");
        assertThat(result.getExtRef()).isEqualTo("EXT-100");
        verify(coreService, times(1)).doSettle(anyString(), any(UUID.class), any());
    }

    @Test
    void settle_duplicateCall_idempotent() {
        SyncSettleRequest request = SyncSettleRequest.builder()
                .sessionRef("sess-101")
                .paymentId(UUID.randomUUID())
                .build();

        // Simulate that a prior call already settled this session
        Settlement doneSentiment = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-101")
                .settleStatus(SettlementStatus.DONE)
                .extRef("EXT-101")
                .retryCount(1)
                .build();

        when(settlementRepository.findBySessionRefAndSettleStatus(eq("sess-101"), eq(SettlementStatus.DONE)))
                .thenReturn(Optional.of(doneSentiment));

        SettlementResponse result = syncService.settle(request);

        // Core service must not be called when the early-return guard fires
        verify(coreService, never()).doSettle(any(), any(), any());
        assertThat(result.getSettleStatus()).isEqualTo("DONE");
        assertThat(result.getExtRef()).isEqualTo("EXT-101");
    }
}
