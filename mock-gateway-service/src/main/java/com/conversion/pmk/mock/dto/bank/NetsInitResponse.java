package com.conversion.pmk.mock.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetsInitResponse {

    private String txnRef;
    private String status;
    private String message;
}
