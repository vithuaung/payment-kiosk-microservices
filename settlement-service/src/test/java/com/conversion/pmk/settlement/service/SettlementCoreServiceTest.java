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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementCoreServiceTest {

    @Mock private SettlementRepository settlementRepository;
    @Mock private SettleAttemptRepository attemptRepository;
    @Mock private SapSettleClient sapSettleClient;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private SettlementCoreService coreService;

    private SapSettleRequest sapRequest;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(coreService, "configuredMaxRetry", 5);

        sapRequest = SapSettleRequest.builder()
                .sessionRef("sess-001")
                .personRef("P-001")
                .payMethod("CASH")
                .items(new ArrayList<>())
                .build();

        // ObjectMapper serialisation is a detail — return empty JSON for all calls
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
    }

    @Test
    void doSettle_newSettlement_callsSapAndSetsDone() {
        // No existing settlement
        when(settlementRepository.findBySessionRef("sess-001")).thenReturn(Optional.empty());

        Settlement saved = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-001")
                .paymentId(UUID.randomUUID())
                .settleStatus(SettlementStatus.PROCESSING)
                .retryCount(0)
                .maxRetry(5)
                .attempts(new ArrayList<>())
                .build();
        when(settlementRepository.save(any())).thenReturn(saved);

        SapSettleResponse sapResponse = new SapSettleResponse("EXT-001", "SUCCESS", "OK");
        when(sapSettleClient.settle(any())).thenReturn(sapResponse);

        when(attemptRepository.save(any())).thenReturn(new SettleAttempt());

        SettlementResponse result = coreService.doSettle("sess-001", UUID.randomUUID(), sapRequest);

        // SAP must be called exactly once
        verify(sapSettleClient, times(1)).settle(any());

        // Capture the settlement saved after SAP returns
        ArgumentCaptor<Settlement> captor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository, atLeastOnce()).save(captor.capture());
        Settlement finalSave = captor.getAllValues().stream()
                .filter(s -> s.getSettleStatus() == SettlementStatus.DONE)
                .findFirst()
                .orElse(null);
        assertThat(finalSave).isNotNull();
        assertThat(finalSave.getExtRef()).isEqualTo("EXT-001");
        assertThat(result).isNotNull();
    }

    @Test
    void doSettle_sapFails_incrementsRetryCount() {
        when(settlementRepository.findBySessionRef("sess-001")).thenReturn(Optional.empty());

        Settlement saved = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-001")
                .paymentId(UUID.randomUUID())
                .settleStatus(SettlementStatus.PROCESSING)
                .retryCount(0)
                .maxRetry(5)
                .attempts(new ArrayList<>())
                .build();
        when(settlementRepository.save(any())).thenReturn(saved);
        when(attemptRepository.save(any())).thenReturn(new SettleAttempt());

        // SAP throws an exception
        when(sapSettleClient.settle(any())).thenThrow(new RuntimeException("SAP timeout"));

        coreService.doSettle("sess-001", UUID.randomUUID(), sapRequest);

        ArgumentCaptor<Settlement> captor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository, atLeastOnce()).save(captor.capture());

        // After failure with retryCount still < maxRetry the status should be PENDING
        Settlement afterFail = captor.getAllValues().stream()
                .filter(s -> s.getSettleStatus() == SettlementStatus.PENDING || s.getSettleStatus() == SettlementStatus.FAILED)
                .findFirst()
                .orElse(null);
        assertThat(afterFail).isNotNull();
        assertThat(afterFail.getRetryCount()).isGreaterThan(0);
    }

    @Test
    void doSettle_maxRetryReached_throwsException() {
        // Existing settlement already failed and has hit the retry ceiling
        Settlement maxed = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-002")
                .paymentId(UUID.randomUUID())
                .settleStatus(SettlementStatus.FAILED)
                .retryCount(5)
                .maxRetry(5)
                .attempts(new ArrayList<>())
                .build();

        when(settlementRepository.findBySessionRef("sess-002")).thenReturn(Optional.of(maxed));

        assertThatThrownBy(() -> coreService.doSettle("sess-002", UUID.randomUUID(), sapRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("Max retries reached");

        // SAP should never be called when max retry is exceeded
        verify(sapSettleClient, never()).settle(any());
    }

    @Test
    void doSettle_alreadyDone_returnsExisting() {
        Settlement done = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-003")
                .paymentId(UUID.randomUUID())
                .settleStatus(SettlementStatus.DONE)
                .extRef("EXT-999")
                .retryCount(1)
                .maxRetry(5)
                .attempts(new ArrayList<>())
                .build();

        when(settlementRepository.findBySessionRef("sess-003")).thenReturn(Optional.of(done));

        SettlementResponse result = coreService.doSettle("sess-003", UUID.randomUUID(), sapRequest);

        // SAP must not be called for an already-done settlement
        verify(sapSettleClient, never()).settle(any());
        assertThat(result.getSettleStatus()).isEqualTo("DONE");
        assertThat(result.getExtRef()).isEqualTo("EXT-999");
    }
}
