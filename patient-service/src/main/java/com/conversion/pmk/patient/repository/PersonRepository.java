package com.conversion.pmk.patient.repository;

import com.conversion.pmk.patient.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Data access for patient identity records
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByIdRef(String idRef);
}
