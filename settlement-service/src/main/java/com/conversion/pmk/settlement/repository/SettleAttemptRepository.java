package com.conversion.pmk.settlement.repository;

import com.conversion.pmk.settlement.entity.SettleAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettleAttemptRepository extends JpaRepository<SettleAttempt, UUID> {

    List<SettleAttempt> findBySettlementSettlementIdOrderByAttemptNoAsc(UUID settlementId);

    int countBySettlementSettlementId(UUID settlementId);
}
