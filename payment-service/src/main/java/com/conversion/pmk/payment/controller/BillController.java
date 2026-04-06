package com.conversion.pmk.payment.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import com.conversion.pmk.payment.service.BillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping("/lookup")
    public ResponseEntity<ApiResponse<BillDetailResponse>> lookupBills(
            @RequestBody @Valid BillLookupRequest request) {
        BillDetailResponse result = billService.getBillDetails(request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
