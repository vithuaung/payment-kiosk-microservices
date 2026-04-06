package com.conversion.pmk.payment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {

    @NotBlank
    private String personRef;

    @NotBlank
    private String terminalCode;

    @NotBlank
    private String payMethod;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmt;

    @Valid
    private List<BillItemRequest> billItems;
}
