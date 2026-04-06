package com.conversion.pmk.mock.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailResponse {

    private String personRef;
    private int billCount;
    private List<BillItem> bills;
}
