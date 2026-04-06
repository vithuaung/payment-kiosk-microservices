package com.conversion.pmk.patient.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Visit summary data returned to callers
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitResponse {

    private UUID visitId;
    private String scheduledAt;
    private String counterCode;
    private String counterName;
    private String queueNo;
    private String visitStatus;
}
