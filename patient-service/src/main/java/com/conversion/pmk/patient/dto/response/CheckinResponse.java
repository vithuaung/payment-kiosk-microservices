package com.conversion.pmk.patient.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Check-in confirmation data returned to callers
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinResponse {

    private UUID checkinId;
    private String checkinType;
    private String queueNo;
    private String locationCode;
    private String checkinAt;
    private String message;
}
