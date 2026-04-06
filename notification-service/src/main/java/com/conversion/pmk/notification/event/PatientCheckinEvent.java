package com.conversion.pmk.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors the event published by patient-service on pmk.patient.registered
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientCheckinEvent {

    private String eventId;
    private String personRef;
    private String checkinId;
    private String checkinType;
    private String locationCode;
    private String checkinAt;
    private long occurredAt;
}
