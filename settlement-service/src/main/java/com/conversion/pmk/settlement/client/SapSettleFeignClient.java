package com.conversion.pmk.settlement.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleRequest;
import com.conversion.pmk.settlement.client.SapSettleClient.SapSettleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Declarative HTTP client for the mock SAP settlement endpoint
@FeignClient(name = "sap-settle-client", url = "${pmk.mock.sap-url}")
public interface SapSettleFeignClient {

    @PostMapping("/mock/sap/bill-post")
    ApiResponse<SapSettleResponse> settle(@RequestBody SapSettleRequest request);
}
