package com.conversion.pmk.patient.service;

import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;

// Defines the check-in operation
public interface CheckinService {

    // Validates patient, notifies NGEMR, persists record, publishes event
    CheckinResponse performCheckin(CheckinRequest request);
}
