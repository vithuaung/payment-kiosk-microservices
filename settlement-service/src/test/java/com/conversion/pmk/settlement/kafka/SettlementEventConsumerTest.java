package com.conversion.pmk.settlement.kafka;

import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.event.PaymentCompletedEvent;
import com.conversion.pmk.settlement.event.PaymentFailedEvent;
import com.conversion.pmk.settlement.event.PaymentInitiatedEvent;
import com.conversion.pmk.settlement.service.SettlementCoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementEventConsumerTest {

    @Mock private SettlementCoreService coreService;
    @Mock private SettlementEventProducer producer;

    @InjectMocks
    private SettlementEventConsumer consumer;

    private PaymentInitiatedEvent buildEvent(String sessionRef) {
        return new PaymentInitiatedEvent(
                UUID.randomUUID().toString(),
                sessionRef,
                "P-300",
                "TERM-01",
                BigDecimal.valueOf(150.00),
                "CASH",
                System.currentTimeMillis()
        );
    }

    @Test
    void onPaymentInitiated_success_publishesCompleted() {
        PaymentInitiatedEvent event = buildEvent("sess-300");

        SettlementResponse doneResponse = SettlementResponse.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-300")
                .settleStatus("DONE")
                .extRef("EXT-300")
                .retryCount(1)
                .build();

        when(coreService.doSettle(anyString(), any(UUID.class), any())).thenReturn(doneResponse);

        consumer.onPaymentInitiated(event);

        ArgumentCaptor<PaymentCompletedEvent> captor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(producer, times(1)).publishPaymentCompleted(captor.capture());
        verify(producer, never()).publishPaymentFailed(any());

        assertThat(captor.getValue().getSessionRef()).isEqualTo("sess-300");
        assertThat(captor.getValue().getExtRef()).isEqualTo("EXT-300");
    }

    @Test
    void onPaymentInitiated_failed_publishesFailed() {
        PaymentInitiatedEvent event = buildEvent("sess-301");

        SettlementResponse failedResponse = SettlementResponse.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-301")
                .settleStatus("FAILED")
                .failReason("SAP unavailable")
                .retryCount(5)
                .build();

        when(coreService.doSettle(anyString(), any(UUID.class), any())).thenReturn(failedResponse);

        consumer.onPaymentInitiated(event);

        ArgumentCaptor<PaymentFailedEvent> captor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(producer, times(1)).publishPaymentFailed(captor.capture());
        verify(producer, never()).publishPaymentCompleted(any());

        assertThat(captor.getValue().getSessionRef()).isEqualTo("sess-301");
        assertThat(captor.getValue().getFailReason()).isEqualTo("SAP unavailable");
        assertThat(captor.getValue().getRetryCount()).isEqualTo(5);
    }
}
