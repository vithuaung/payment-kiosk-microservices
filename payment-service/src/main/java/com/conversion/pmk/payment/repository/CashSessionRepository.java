package com.conversion.pmk.payment.repository;

import com.conversion.pmk.payment.entity.CashSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashSessionRepository extends JpaRepository<CashSession, UUID> {

    Optional<CashSession> findByPaymentPaymentId(UUID paymentId);
}
