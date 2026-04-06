package com.conversion.pmk.payment.client;

import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.payment.dto.request.BillItemRequest;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

// HTTP client for the mock SAP billing gateway
@Slf4j
@Component
@RequiredArgsConstructor
public class SapBillingClient {

    private final RestTemplate restTemplate;

    @Value("${pmk.mock.sap-url}")
    private String sapUrl;

    // Fetch outstanding bill details for a patient
    public BillDetailResponse lookupBills(String personRef, String orgCode) {
        String url = sapUrl + "/mock/sap/bill-details";
        BillLookupRequest body = BillLookupRequest.builder()
                .personRef(personRef)
                .orgCode(orgCode)
                .build();
        try {
            return restTemplate.postForObject(url, body, BillDetailResponse.class);
        } catch (RestClientException ex) {
            log.error("SAP bill lookup failed for personRef={}: {}", personRef, ex.getMessage());
            throw new PmkException("Billing service unavailable", "SAP_UNAVAILABLE", ex);
        }
    }

    // Post paid bill items back to SAP after payment completes
    public BillPostResponse postBills(String sessionRef, String personRef,
                                      List<BillItemRequest> items, String payMethod) {
        String url = sapUrl + "/mock/sap/bill-post";
        Map<String, Object> body = Map.of(
                "sessionRef", sessionRef,
                "personRef", personRef,
                "items", items,
                "payMethod", payMethod
        );
        try {
            return restTemplate.postForObject(url, body, BillPostResponse.class);
        } catch (RestClientException ex) {
            log.error("SAP bill post failed for sessionRef={}: {}", sessionRef, ex.getMessage());
            throw new PmkException("Billing service unavailable", "SAP_UNAVAILABLE", ex);
        }
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
