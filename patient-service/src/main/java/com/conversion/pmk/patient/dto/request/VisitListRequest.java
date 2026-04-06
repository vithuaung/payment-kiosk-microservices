package com.conversion.pmk.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request body for listing a patient's visits with optional status filter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitListRequest {

    private String idRef;

    // Optional — filter by VisitStatus name; returns all statuses when null
    private String visitStatus;
}
