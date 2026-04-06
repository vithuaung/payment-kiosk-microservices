package com.conversion.pmk.payment.service;

import com.conversion.pmk.common.enums.SessionStatus;
import com.conversion.pmk.common.exception.PaymentException;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.payment.dto.request.CashSessionRequest;
import com.conversion.pmk.payment.dto.response.CashSessionResponse;
import com.conversion.pmk.payment.entity.CashSession;
import com.conversion.pmk.payment.entity.Payment;
import com.conversion.pmk.payment.repository.CashSessionRepository;
import com.conversion.pmk.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashSessionServiceImpl implements CashSessionService {

    private final CashSessionRepository cashSessionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public CashSessionResponse openSession(String sessionRef) {
        // 1. Load payment
        Payment payment = paymentRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", sessionRef));

        // 2. Guard: reject if an OPEN session already exists for this payment
        cashSessionRepository.findByPaymentPaymentId(payment.getPaymentId())
                .ifPresent(existing -> {
                    if (existing.getSessionStatus() == SessionStatus.OPEN) {
                        throw new PaymentException(
                                "Cash session already open for sessionRef=" + sessionRef,
                                "CASH_SESSION_ALREADY_OPEN");
                    }
                });

        // 3. Build and save
        CashSession session = CashSession.builder()
                .payment(payment)
                .sessionStatus(SessionStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .insertedAmt(BigDecimal.ZERO)
                .returnedAmt(BigDecimal.ZERO)
                .build();

        CashSession saved = cashSessionRepository.save(session);
        log.debug("CashSession opened for sessionRef={}", sessionRef);
        return toResponse(saved, sessionRef);
    }

    @Override
    @Transactional
    public CashSessionResponse updateSession(CashSessionRequest request) {
        // 1. Load payment then session
        Payment payment = paymentRepository.findBySessionRef(request.getSessionRef())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", request.getSessionRef()));

        CashSession session = cashSessionRepository.findByPaymentPaymentId(payment.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("CashSession", request.getSessionRef()));

        // 2. Update amounts
        if (request.getInsertedAmt() != null) {
            session.setInsertedAmt(request.getInsertedAmt());
        }
        if (request.getReturnedAmt() != null) {
            session.setReturnedAmt(request.getReturnedAmt());
        }

        CashSession saved = cashSessionRepository.save(session);
        log.debug("CashSession updated for sessionRef={}", request.getSessionRef());
        return toResponse(saved, request.getSessionRef());
    }

    @Override
    @Transactional
    public CashSessionResponse closeSession(String sessionRef, BigDecimal insertedAmt, BigDecimal returnedAmt) {
        // 1. Load payment then session
        Payment payment = paymentRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", sessionRef));

        CashSession session = cashSessionRepository.findByPaymentPaymentId(payment.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("CashSession", sessionRef));

        // 2. Set closure fields
        if (insertedAmt != null) {
            session.setInsertedAmt(insertedAmt);
        }
        if (returnedAmt != null) {
            session.setReturnedAmt(returnedAmt);
        }
        session.setSessionStatus(SessionStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());

        CashSession saved = cashSessionRepository.save(session);
        log.debug("CashSession closed for sessionRef={}", sessionRef);
        return toResponse(saved, sessionRef);
    }

    // Inline mapping — no MapStruct needed here
    private CashSessionResponse toResponse(CashSession session, String sessionRef) {
        return CashSessionResponse.builder()
                .cashId(session.getCashId())
                .sessionRef(sessionRef)
                .insertedAmt(session.getInsertedAmt())
                .returnedAmt(session.getReturnedAmt())
                .sessionStatus(session.getSessionStatus() != null ? session.getSessionStatus().name() : null)
                .build();
    }
}
