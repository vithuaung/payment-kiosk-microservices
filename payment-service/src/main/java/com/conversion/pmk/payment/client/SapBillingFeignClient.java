package com.conversion.pmk.payment.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.payment.client.SapBillingClient.BillPostResponse;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

// Declarative HTTP client for the mock SAP billing gateway
@FeignClient(name = "sap-billing-client", url = "${pmk.mock.sap-url}")
public interface SapBillingFeignClient {

    @PostMapping("/mock/sap/bill-details")
    ApiResponse<BillDetailResponse> lookupBills(@RequestBody BillLookupRequest body);

    @PostMapping("/mock/sap/bill-post")
    ApiResponse<BillPostResponse> postBills(@RequestBody Map<String, Object> body);
}
