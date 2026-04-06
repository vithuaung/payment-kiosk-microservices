package com.conversion.pmk.patient.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Patient identity data returned to callers
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {

    private UUID personId;
    private String idRef;
    private String fullName;
    private String birthDate;
    private String mobileNo;
    private String emailAddr;
}
