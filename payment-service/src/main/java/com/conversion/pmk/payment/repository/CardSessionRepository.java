package com.conversion.pmk.payment.repository;

import com.conversion.pmk.payment.entity.CardSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardSessionRepository extends JpaRepository<CardSession, UUID> {

    Optional<CardSession> findByPaymentPaymentId(UUID paymentId);
}
