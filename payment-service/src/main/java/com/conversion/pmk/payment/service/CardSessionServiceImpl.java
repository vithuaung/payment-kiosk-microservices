package com.conversion.pmk.payment.service;

import com.conversion.pmk.common.enums.SessionStatus;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.payment.dto.request.CardSessionRequest;
import com.conversion.pmk.payment.dto.response.CardSessionResponse;
import com.conversion.pmk.payment.entity.CardSession;
import com.conversion.pmk.payment.entity.Payment;
import com.conversion.pmk.payment.repository.CardSessionRepository;
import com.conversion.pmk.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSessionServiceImpl implements CardSessionService {

    private final CardSessionRepository cardSessionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public CardSessionResponse openSession(String sessionRef) {
        // 1. Load payment
        Payment payment = paymentRepository.findBySessionRef(sessionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", sessionRef));

        // 2. Build and save
        CardSession session = CardSession.builder()
                .payment(payment)
                .sessionStatus(SessionStatus.OPEN)
                .startedAt(LocalDateTime.now())
                .build();

        CardSession saved = cardSessionRepository.save(session);
        log.debug("CardSession opened for sessionRef={}", sessionRef);
        return toResponse(saved, sessionRef);
    }

    @Override
    @Transactional
    public CardSessionResponse updateSession(CardSessionRequest request) {
        // 1. Load payment then card session
        Payment payment = paymentRepository.findBySessionRef(request.getSessionRef())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", request.getSessionRef()));

        CardSession session = cardSessionRepository.findByPaymentPaymentId(payment.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("CardSession", request.getSessionRef()));

        // 2. Update fields
        if (request.getCardNetwork() != null) {
            session.setCardNetwork(request.getCardNetwork());
        }
        if (request.getApprovalRef() != null) {
            session.setApprovalRef(request.getApprovalRef());
        }
        if (request.getTerminalRef() != null) {
            session.setTerminalRef(request.getTerminalRef());
        }
        if (request.getSessionStatus() != null) {
            SessionStatus newStatus = SessionStatus.valueOf(request.getSessionStatus());
            session.setSessionStatus(newStatus);

            // 3. If terminal status, record finish time
            if (newStatus == SessionStatus.APPROVED || newStatus == SessionStatus.DECLINED) {
                session.setFinishedAt(LocalDateTime.now());
            }
        }

        CardSession saved = cardSessionRepository.save(session);
        log.debug("CardSession updated for sessionRef={}", request.getSessionRef());
        return toResponse(saved, request.getSessionRef());
    }

    // Inline mapping
    private CardSessionResponse toResponse(CardSession session, String sessionRef) {
        return CardSessionResponse.builder()
                .cardId(session.getCardId())
                .sessionRef(sessionRef)
                .cardNetwork(session.getCardNetwork())
                .approvalRef(session.getApprovalRef())
                .sessionStatus(session.getSessionStatus() != null ? session.getSessionStatus().name() : null)
                .build();
    }
}
