package com.conversion.pmk.mock.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZeroBillRequest {

    private String sessionRef;
    private String personRef;
    private String orgCode;
}
