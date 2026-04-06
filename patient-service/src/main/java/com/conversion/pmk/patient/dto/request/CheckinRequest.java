package com.conversion.pmk.patient.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request body for performing a patient check-in
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRequest {

    @NotBlank(message = "idRef must not be blank")
    private String idRef;

    // Optional UUID string — linked visit for appointment check-ins
    private String visitId;

    @NotBlank(message = "checkinType must not be blank")
    private String checkinType;

    @NotBlank(message = "locationCode must not be blank")
    private String locationCode;
}
