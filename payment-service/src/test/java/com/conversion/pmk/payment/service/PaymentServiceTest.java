package com.conversion.pmk.payment.service;

import com.conversion.pmk.common.enums.PaymentMethod;
import com.conversion.pmk.common.enums.PaymentStatus;
import com.conversion.pmk.common.exception.PaymentException;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.payment.cache.BillCacheService;
import com.conversion.pmk.payment.client.SapBillingClient;
import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import com.conversion.pmk.payment.entity.Payment;
import com.conversion.pmk.payment.entity.PayMethod;
import com.conversion.pmk.payment.entity.Terminal;
import com.conversion.pmk.payment.event.PaymentInitiatedEvent;
import com.conversion.pmk.payment.kafka.PaymentEventProducer;
import com.conversion.pmk.payment.mapper.BillItemMapper;
import com.conversion.pmk.payment.mapper.PaymentMapper;
import com.conversion.pmk.payment.repository.BillItemRepository;
import com.conversion.pmk.payment.repository.PayMethodRepository;
import com.conversion.pmk.payment.repository.PaymentRepository;
import com.conversion.pmk.payment.repository.TerminalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BillItemRepository billItemRepository;
    @Mock private PayMethodRepository payMethodRepository;
    @Mock private TerminalRepository terminalRepository;
    @Mock private PaymentMapper paymentMapper;
    @Mock private BillItemMapper billItemMapper;
    @Mock private PaymentEventProducer paymentEventProducer;
    @Mock private BillCacheService billCacheService;
    @Mock private SapBillingClient sapBillingClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Terminal sampleTerminal;
    private PayMethod samplePayMethod;
    private InitiatePaymentRequest initiateRequest;

    @BeforeEach
    void setUp() {
        sampleTerminal = Terminal.builder()
                .terminalId(UUID.randomUUID())
                .terminalCode("TERM-001")
                .isActive(true)
                .build();

        samplePayMethod = PayMethod.builder()
                .methodId(UUID.randomUUID())
                .methodCode("CASH")
                .methodGroup("CASH")
                .isActive(true)
                .build();

        initiateRequest = InitiatePaymentRequest.builder()
                .personRef("P-001")
                .terminalCode("TERM-001")
                .payMethod("CASH")
                .totalAmt(BigDecimal.valueOf(100.00))
                .billItems(Collections.emptyList())
                .build();
    }

    @Test
    void initiate_success_createsPendingPayment() {
        // Arrange
        when(terminalRepository.findByTerminalCodeAndIsActiveTrue("TERM-001"))
                .thenReturn(Optional.of(sampleTerminal));
        when(payMethodRepository.findByMethodCodeAndIsActiveTrue("CASH"))
                .thenReturn(Optional.of(samplePayMethod));

        Payment savedPayment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(UUID.randomUUID().toString())
                .terminalCode("TERM-001")
                .personRef("P-001")
                .totalAmt(BigDecimal.valueOf(100.00))
                .payMethod(PaymentMethod.CASH)
                .payStatus(PaymentStatus.PENDING)
                .billItems(Collections.emptyList())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse expectedResponse = PaymentResponse.builder()
                .sessionRef(savedPayment.getSessionRef())
                .payStatus("PENDING")
                .build();
        when(paymentMapper.toResponse(savedPayment)).thenReturn(expectedResponse);

        // Act
        PaymentResponse result = paymentService.initiate(initiateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPayStatus()).isEqualTo("PENDING");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getPayStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(captor.getValue().getPersonRef()).isEqualTo("P-001");
    }

    @Test
    void initiate_terminalNotFound_throwsException() {
        // Arrange
        when(terminalRepository.findByTerminalCodeAndIsActiveTrue("TERM-001"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> paymentService.initiate(initiateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Terminal");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void startProcessing_success_publishesKafkaEvent() {
        // Arrange
        String sessionRef = "sess-001";
        Payment pendingPayment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .personRef("P-001")
                .terminalCode("TERM-001")
                .totalAmt(BigDecimal.valueOf(100.00))
                .payMethod(PaymentMethod.CASH)
                .payStatus(PaymentStatus.PENDING)
                .build();

        Payment processingPayment = Payment.builder()
                .paymentId(pendingPayment.getPaymentId())
                .sessionRef(sessionRef)
                .personRef("P-001")
                .terminalCode("TERM-001")
                .totalAmt(BigDecimal.valueOf(100.00))
                .payMethod(PaymentMethod.CASH)
                .payStatus(PaymentStatus.PROCESSING)
                .build();

        when(paymentRepository.findBySessionRef(sessionRef)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(processingPayment);

        PaymentResponse expectedResponse = PaymentResponse.builder()
                .sessionRef(sessionRef)
                .payStatus("PROCESSING")
                .build();
        when(paymentMapper.toResponse(processingPayment)).thenReturn(expectedResponse);

        // Act
        PaymentResponse result = paymentService.startProcessing(sessionRef);

        // Assert
        assertThat(result.getPayStatus()).isEqualTo("PROCESSING");

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getPayStatus()).isEqualTo(PaymentStatus.PROCESSING);

        verify(paymentEventProducer).publishPaymentInitiated(any(PaymentInitiatedEvent.class));
        verify(billCacheService).evictPaymentSession(sessionRef);
    }

    @Test
    void startProcessing_alreadyProcessing_throwsException() {
        // Arrange
        String sessionRef = "sess-002";
        Payment processingPayment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .payStatus(PaymentStatus.PROCESSING)
                .build();

        when(paymentRepository.findBySessionRef(sessionRef)).thenReturn(Optional.of(processingPayment));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.startProcessing(sessionRef))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("already processing");

        verify(paymentRepository, never()).save(any());
        verify(paymentEventProducer, never()).publishPaymentInitiated(any());
    }

    @Test
    void complete_success_setsDoneStatus() {
        // Arrange
        String sessionRef = "sess-003";
        Payment processingPayment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .personRef("P-001")
                .terminalCode("TERM-001")
                .totalAmt(BigDecimal.valueOf(100.00))
                .payMethod(PaymentMethod.CASH)
                .payStatus(PaymentStatus.PROCESSING)
                .build();

        Payment donePayment = Payment.builder()
                .paymentId(processingPayment.getPaymentId())
                .sessionRef(sessionRef)
                .personRef("P-001")
                .terminalCode("TERM-001")
                .totalAmt(BigDecimal.valueOf(100.00))
                .paidAmt(BigDecimal.valueOf(100.00))
                .changeAmt(BigDecimal.ZERO)
                .payMethod(PaymentMethod.CASH)
                .payStatus(PaymentStatus.DONE)
                .build();

        when(paymentRepository.findBySessionRef(sessionRef)).thenReturn(Optional.of(processingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(donePayment);
        when(billItemRepository.findByPaymentPaymentId(any(UUID.class))).thenReturn(Collections.emptyList());

        PaymentResponse expectedResponse = PaymentResponse.builder()
                .sessionRef(sessionRef)
                .payStatus("DONE")
                .paidAmt(BigDecimal.valueOf(100.00))
                .changeAmt(BigDecimal.ZERO)
                .build();
        when(paymentMapper.toResponse(donePayment)).thenReturn(expectedResponse);

        com.conversion.pmk.payment.dto.request.CompletePaymentRequest completeRequest =
                com.conversion.pmk.payment.dto.request.CompletePaymentRequest.builder()
                        .sessionRef(sessionRef)
                        .paidAmt(BigDecimal.valueOf(100.00))
                        .changeAmt(BigDecimal.ZERO)
                        .build();

        // Act
        PaymentResponse result = paymentService.complete(completeRequest);

        // Assert
        assertThat(result.getPayStatus()).isEqualTo("DONE");
        assertThat(result.getPaidAmt()).isEqualByComparingTo(BigDecimal.valueOf(100.00));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getPayStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(captor.getValue().getPaidAmt()).isEqualByComparingTo(BigDecimal.valueOf(100.00));

        verify(billCacheService).evictPaymentSession(sessionRef);
    }
}
