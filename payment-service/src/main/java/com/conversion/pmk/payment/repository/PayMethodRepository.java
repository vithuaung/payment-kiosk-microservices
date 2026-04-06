package com.conversion.pmk.payment.repository;

import com.conversion.pmk.payment.entity.PayMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayMethodRepository extends JpaRepository<PayMethod, UUID> {

    Optional<PayMethod> findByMethodCodeAndIsActiveTrue(String methodCode);
}
