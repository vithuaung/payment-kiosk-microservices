package com.conversion.pmk.mock.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardChargeResponse {

    private String approvalRef;
    private String cardNetwork;
    private String status;
    private String message;
}
