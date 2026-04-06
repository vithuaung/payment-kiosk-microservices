package com.conversion.pmk.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSessionRequest {

    @NotBlank
    private String sessionRef;

    private String cardNetwork;
    private String approvalRef;
    private String terminalRef;
    private String sessionStatus;
}
