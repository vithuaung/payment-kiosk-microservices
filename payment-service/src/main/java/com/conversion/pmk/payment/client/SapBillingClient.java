package com.conversion.pmk.payment.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.payment.dto.request.BillItemRequest;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// Thin wrapper around SapBillingFeignClient — handles ApiResponse unwrapping,
// circuit breaking, and retry with fallback on failure.
@Slf4j
@Component
@RequiredArgsConstructor
public class SapBillingClient {

    private final SapBillingFeignClient feignClient;

    @CircuitBreaker(name = "sap-billing", fallbackMethod = "lookupBillsFallback")
    @Retry(name = "sap-billing")
    public BillDetailResponse lookupBills(String personRef, String orgCode) {
        BillLookupRequest body = BillLookupRequest.builder()
                .personRef(personRef)
                .orgCode(orgCode)
                .build();
        ApiResponse<BillDetailResponse> resp = feignClient.lookupBills(body);
        return resp != null ? resp.getData() : null;
    }

    @CircuitBreaker(name = "sap-billing", fallbackMethod = "postBillsFallback")
    @Retry(name = "sap-billing")
    public BillPostResponse postBills(String sessionRef, String personRef,
                                      List<BillItemRequest> items, String payMethod) {
        Map<String, Object> body = Map.of(
                "sessionRef", sessionRef,
                "personRef", personRef,
                "items", items,
                "payMethod", payMethod
        );
        ApiResponse<BillPostResponse> resp = feignClient.postBills(body);
        return resp != null ? resp.getData() : null;
    }

    // ─── Fallbacks ────────────────────────────────────────────────────────────

    BillDetailResponse lookupBillsFallback(String personRef, String orgCode, Exception ex) {
        log.error("SAP bill lookup unavailable for personRef={}: {}", personRef, ex.getMessage());
        throw new PmkException("Billing service unavailable", "SAP_UNAVAILABLE", ex);
    }

    BillPostResponse postBillsFallback(String sessionRef, String personRef,
                                       List<BillItemRequest> items, String payMethod, Exception ex) {
        log.error("SAP bill post unavailable for sessionRef={}: {}", sessionRef, ex.getMessage());
        throw new PmkException("Billing service unavailable", "SAP_UNAVAILABLE", ex);
    }

    // Response shape returned by mock SAP bill-post endpoint
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPostResponse {
        private String extRef;
        private String postStatus;
        private String message;
    }
}
