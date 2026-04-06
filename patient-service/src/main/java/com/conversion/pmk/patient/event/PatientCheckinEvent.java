package com.conversion.pmk.patient.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Kafka event emitted after a patient check-in is recorded
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCheckinEvent {

    // UUID string generated at publish time
    private String eventId;
    private String personRef;
    private String checkinId;
    private String checkinType;
    private String locationCode;
    private String checkinAt;
    private long occurredAt;
}
