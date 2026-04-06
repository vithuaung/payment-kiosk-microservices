package com.conversion.pmk.patient.repository;

import com.conversion.pmk.patient.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Data access for location reference data
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByLocationCode(String code);
}
