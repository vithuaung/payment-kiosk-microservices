package com.conversion.pmk.patient.repository;

import com.conversion.pmk.patient.entity.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Data access for check-in transaction records
public interface CheckinRepository extends JpaRepository<Checkin, UUID> {

    List<Checkin> findByPersonPersonIdOrderByCheckinAtDesc(UUID personId);
}
