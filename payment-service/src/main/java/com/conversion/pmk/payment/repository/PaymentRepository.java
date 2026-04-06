package com.conversion.pmk.payment.repository;

import com.conversion.pmk.common.enums.PaymentStatus;
import com.conversion.pmk.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findBySessionRef(String sessionRef);

    List<Payment> findByPersonRefAndPayStatus(String personRef, PaymentStatus payStatus);
}
