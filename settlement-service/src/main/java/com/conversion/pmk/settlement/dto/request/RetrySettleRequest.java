package com.conversion.pmk.settlement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request body for manually retrying a failed settlement
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrySettleRequest {

    @NotBlank(message = "sessionRef is required")
    private String sessionRef;
}
