package com.conversion.pmk.payment.dto.response;

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
public class BillDetailResponse {

    private String personRef;
    private String personName;
    private List<BillItemResponse> bills;
    private BigDecimal totalPayable;
}
