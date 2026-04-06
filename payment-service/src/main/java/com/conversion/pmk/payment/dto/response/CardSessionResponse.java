package com.conversion.pmk.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSessionResponse {

    private UUID cardId;
    private String sessionRef;
    private String cardNetwork;
    private String approvalRef;
    private String sessionStatus;
}
