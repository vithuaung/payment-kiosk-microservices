package com.conversion.pmk.patient.service;

import com.conversion.pmk.patient.dto.response.VisitResponse;

import java.util.List;
import java.util.UUID;

// Defines operations for retrieving and syncing patient visits
public interface VisitService {

    // Returns visits for a patient; status filter is optional
    List<VisitResponse> getVisits(String idRef, String status);

    // Fetches a specific visit from NGEMR and persists it locally
    VisitResponse syncFromNgemr(String idRef, UUID visitId);
}
