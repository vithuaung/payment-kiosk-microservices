package com.conversion.pmk.patient.service;

import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;

// Defines person lookup and registration operations
public interface PersonService {

    // Returns person from DB or NGEMR; saves locally if not yet persisted
    PersonResponse findByIdRef(String idRef);

    // Upsert: fetches from NGEMR and persists if not already registered
    PersonResponse register(PersonLookupRequest request);
}
