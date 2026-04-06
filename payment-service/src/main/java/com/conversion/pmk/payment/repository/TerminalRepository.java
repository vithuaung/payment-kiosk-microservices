package com.conversion.pmk.payment.repository;

import com.conversion.pmk.payment.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, UUID> {

    Optional<Terminal> findByTerminalCodeAndIsActiveTrue(String terminalCode);
}
