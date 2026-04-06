package com.conversion.pmk.mock.dto.ngemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonLookupResponse {

    private String idRef;
    private String fullName;
    private String birthDate;
    private String mobileNo;
    private String emailAddr;
}
