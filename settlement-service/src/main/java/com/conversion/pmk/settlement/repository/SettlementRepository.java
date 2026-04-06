package com.conversion.pmk.settlement.repository;

import com.conversion.pmk.common.enums.SettlementStatus;
import com.conversion.pmk.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    Optional<Settlement> findBySessionRef(String sessionRef);

    List<Settlement> findBySettleStatus(SettlementStatus status);

    Optional<Settlement> findBySessionRefAndSettleStatus(String sessionRef, SettlementStatus status);
}
