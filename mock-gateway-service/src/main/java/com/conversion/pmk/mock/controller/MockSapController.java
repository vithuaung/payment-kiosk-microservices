package com.conversion.pmk.mock.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.mock.config.FailureSimulator;
import com.conversion.pmk.mock.dto.sap.BillDetailRequest;
import com.conversion.pmk.mock.dto.sap.BillDetailResponse;
import com.conversion.pmk.mock.dto.sap.BillItem;
import com.conversion.pmk.mock.dto.sap.BillPostRequest;
import com.conversion.pmk.mock.dto.sap.BillPostResponse;
import com.conversion.pmk.mock.dto.sap.ZeroBillRequest;
import com.conversion.pmk.mock.dto.sap.ZeroBillResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// Simulates the SAP billing system endpoints
@Slf4j
@RestController
@RequestMapping("/mock/sap")
@RequiredArgsConstructor
public class MockSapController {

    private final FailureSimulator failureSimulator;

    private static final Random RANDOM = new Random();

    // Returns 1-3 mock outstanding bills for the given person
    @PostMapping("/bill-details")
    public ApiResponse<BillDetailResponse> getBillDetails(@RequestBody BillDetailRequest request) {
        log.debug("SAP bill-details request for personRef={}", request.getPersonRef());

        if (failureSimulator.shouldFail()) {
            log.warn("SAP bill-details simulated failure");
            return ApiResponse.fail("SAP unavailable", "SAP_ERROR");
        }

        List<BillItem> bills = buildMockBills(request.getPersonRef());
        BillDetailResponse response = BillDetailResponse.builder()
                .personRef(request.getPersonRef())
                .billCount(bills.size())
                .bills(bills)
                .build();

        return ApiResponse.ok(response);
    }

    // Posts bill payments to SAP and returns an external reference
    @PostMapping("/bill-post")
    public ApiResponse<BillPostResponse> postBill(@RequestBody BillPostRequest request) {
        log.debug("SAP bill-post request for sessionRef={}", request.getSessionRef());

        if (failureSimulator.shouldFail()) {
            log.warn("SAP bill-post simulated failure");
            return ApiResponse.fail("SAP posting failed", "SAP_POST_ERROR");
        }

        String extRef = "SAP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BillPostResponse response = BillPostResponse.builder()
                .extRef(extRef)
                .postStatus("POSTED")
                .message("Bills posted successfully")
                .build();

        return ApiResponse.ok(response);
    }

    // Issues a zero-value receipt; never fails as SAP accepts zero amounts unconditionally
    @PostMapping("/zero-bill")
    public ApiResponse<ZeroBillResponse> zeroBill(@RequestBody ZeroBillRequest request) {
        log.debug("SAP zero-bill request for sessionRef={}", request.getSessionRef());

        String extRef = "SAP-ZERO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ZeroBillResponse response = ZeroBillResponse.builder()
                .extRef(extRef)
                .status("ZERO_POSTED")
                .build();

        return ApiResponse.ok(response);
    }

    // Generates 1 to 3 dummy bill lines
    private List<BillItem> buildMockBills(String personRef) {
        int count = 1 + RANDOM.nextInt(3);
        List<BillItem> bills = new ArrayList<>();
        String today = LocalDate.now().toString();

        for (int i = 1; i <= count; i++) {
            BigDecimal amt = BigDecimal.valueOf(50 + RANDOM.nextInt(450));
            bills.add(BillItem.builder()
                    .billRef("BILL-" + personRef.substring(0, Math.min(4, personRef.length())).toUpperCase() + "-" + i)
                    .billSeq(i)
                    .billDesc("Consultation charge " + i)
                    .billAmt(amt)
                    .payableAmt(amt)
                    .billDate(today)
                    .billStatus("OUTSTANDING")
                    .build());
        }
        return bills;
    }
}
