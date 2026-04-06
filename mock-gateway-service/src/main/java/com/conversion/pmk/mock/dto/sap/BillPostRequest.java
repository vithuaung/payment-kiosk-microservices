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
public class BillPostRequest {

    private String sessionRef;
    private String personRef;
    private List<BillItemRequest> items;
    private String payMethod;
}
