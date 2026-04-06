package com.conversion.pmk.patient.repository;

import com.conversion.pmk.common.enums.VisitStatus;
import com.conversion.pmk.patient.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Data access for patient visit records
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    List<Visit> findByPersonPersonIdAndVisitStatus(UUID personId, VisitStatus status);
}
