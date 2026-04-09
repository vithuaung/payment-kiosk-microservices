package com.conversion.pmk.payment.service;

import com.conversion.pmk.common.enums.PaymentMethod;
import com.conversion.pmk.common.enums.PaymentStatus;
import com.conversion.pmk.common.exception.PaymentException;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.payment.cache.BillCacheService;
import com.conversion.pmk.payment.client.SapBillingClient;
import com.conversion.pmk.payment.dto.request.BillItemRequest;
import com.conversion.pmk.payment.dto.request.CompletePaymentRequest;
import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import com.conversion.pmk.payment.entity.BillItem;
import com.conversion.pmk.payment.entity.Payment;
import com.conversion.pmk.payment.event.PaymentInitiatedEvent;
import com.conversion.pmk.payment.kafka.PaymentEventProducer;
import com.conversion.pmk.payment.mapper.BillItemMapper;
import com.conversion.pmk.payment.mapper.PaymentMapper;
import com.conversion.pmk.payment.repository.BillItemRepository;
import com.conversion.pmk.payment.repository.PayMethodRepository;
import com.conversion.pmk.payment.repository.PaymentRepository;
import com.conversion.pmk.payment.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillItemRepository billItemRepository;
    private final PayMethodRepository payMethodRepository;
    private final TerminalRepository terminalRepository;
    private final PaymentMapper paymentMapper;
    private final BillItemMapper billItemMapper;
    private final PaymentEventProducer paymentEventProducer;
    private final BillCacheService billCacheService;
    private final SapBillingClient sapBillingClient;

    @Override
    @Transactional
    public PaymentResponse initiate(InitiatePaymentRequest request) {
        // 1. Validate terminal
        terminalRepository.findByTerminalCodeAndIsActiveTrue(request.getTerminalCode())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal", request.getTerminalCode()));

        // 2. Validate payment method
        payMethodRepository.findByMethodCodeAndIsActiveTrue(request.getPayMethod())
                .orElseThrow(() -> new ResourceNotFoundException("PayMethod", request.getPayMethod()));

        // 3. Build Payment entity
        Payment payment = Payment.builder()
                .sessionRef(UUID.randomUUID().toString())
                .terminalCode(request.getTerminalCode())
                .personRef(request.getPersonRef())
                .totalAmt(request.getTotalAmt())
                .payMethod(PaymentMethod.valueOf(request.getPayMethod()))
                .payStatus(PaymentStatus.PENDING)
                .build();

        // 4. Save payment
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Payment saved with sessionRef={} status=PENDING", savedPayment.getSessionRef());

        // 5. Save bill items
        if (request.getBillItems() != null && !request.getBillItems().isEmpty()) {
            List<BillItem> items = request.getBillItems().stream()
                    .map(itemReq -> {
                        BillItem item = billItemMapper.toEntity(itemReq);
                        item.setPayment(savedPayment);
                        return item;
                    })
                    .collect(Collectors.toList());
            billItemRepository.saveAll(items);
            savedPayment.setBillItems(items);
        } else {
            savedPayment.setBillItems(Collections.emptyList());
        }

        // 6. Return response
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse startProcessing(String sessionRef) {
        // 1. Load payment
        Payment payment = paymentRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", sessionRef));

        // 2. Guard: must be PENDING
        if (payment.getPayStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Payment already processing", "PAYMENT_INVALID_STATE");
        }

        // 3. Transition to PROCESSING
        payment.setPayStatus(PaymentStatus.PROCESSING);
        payment.setStartedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        log.debug("Payment sessionRef={} transitioned to PROCESSING", sessionRef);

        // 4. Publish Kafka event
        PaymentInitiatedEvent event = PaymentInitiatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(saved.getPaymentId().toString())
                .sessionRef(saved.getSessionRef())
                .personRef(saved.getPersonRef())
                .terminalCode(saved.getTerminalCode())
                .totalAmt(saved.getTotalAmt())
                .payMethod(saved.getPayMethod().name())
                .occurredAt(System.currentTimeMillis())
                .build();
        paymentEventProducer.publishPaymentInitiated(event);

        // 5. Evict payment session cache
        billCacheService.evictPaymentSession(sessionRef);

        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse complete(CompletePaymentRequest request) {
        // 1. Load payment
        Payment payment = paymentRepository.findBySessionRef(request.getSessionRef())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", request.getSessionRef()));

        // 2. Guard: must be PROCESSING
        if (payment.getPayStatus() != PaymentStatus.PROCESSING) {
            throw new PaymentException("Payment not in processing state", "PAYMENT_INVALID_STATE");
        }

        // 3. Set amounts and status
        payment.setPaidAmt(request.getPaidAmt());
        payment.setChangeAmt(request.getChangeAmt());
        payment.setPayStatus(PaymentStatus.DONE);
        payment.setFinishedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        log.debug("Payment sessionRef={} completed (DONE)", request.getSessionRef());

        // 4. Notify SAP — fire and forget; failure must not roll back the payment
        try {
            List<BillItemRequest> billItemRequests = billItemRepository
                    .findByPaymentPaymentId(saved.getPaymentId())
                    .stream()
                    .map(item -> BillItemRequest.builder()
                            .billRef(item.getBillRef())
                            .billSeq(item.getBillSeq())
                            .billedAmt(item.getBilledAmt())
                            .payableAmt(item.getPayableAmt())
                            .orgCode(item.getOrgCode())
                            .caseRef(item.getCaseRef())
                            .build())
                    .collect(Collectors.toList());

            sapBillingClient.postBills(
                    saved.getSessionRef(),
                    saved.getPersonRef(),
                    billItemRequests,
                    saved.getPayMethod().name()
            );
        } catch (Exception ex) {
            log.error("SAP bill post failed for sessionRef={} — payment will not be rolled back: {}",
                    request.getSessionRef(), ex.getMessage(), ex);
        }

        // 5. Evict cache
        billCacheService.evictPaymentSession(request.getSessionRef());

        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getBySessionRef(String sessionRef) {
        // 1. Check cache
        Optional<PaymentResponse> cached = billCacheService.getCachedPaymentSession(sessionRef);
        if (cached.isPresent()) {
            log.debug("Cache hit for payment sessionRef={}", sessionRef);
            return cached.get();
        }

        // 2. Load from DB
        Payment payment = paymentRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", sessionRef));

        PaymentResponse response = paymentMapper.toResponse(payment);

        // 3. Cache and return
        billCacheService.cachePaymentSession(sessionRef, response);
        return response;
    }
}
