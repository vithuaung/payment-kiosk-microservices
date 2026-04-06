package com.conversion.pmk.patient.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request body for patient lookup by identity reference
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonLookupRequest {

    @NotBlank(message = "idRef must not be blank")
    private String idRef;
}
