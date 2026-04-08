package com.conversion.pmk.payment.client;

import com.conversion.pmk.common.dto.ApiResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
            ResponseEntity<ApiResponse<BillDetailResponse>> response = restTemplate.exchange(
                    url, HttpMethod.POST, buildRequest(body),
                    new ParameterizedTypeReference<ApiResponse<BillDetailResponse>>() {});
            ApiResponse<BillDetailResponse> apiResp = response.getBody();
            return apiResp != null ? apiResp.getData() : null;
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
            ResponseEntity<ApiResponse<BillPostResponse>> response = restTemplate.exchange(
                    url, HttpMethod.POST, buildRequest(body),
                    new ParameterizedTypeReference<ApiResponse<BillPostResponse>>() {});
            ApiResponse<BillPostResponse> apiResp = response.getBody();
            return apiResp != null ? apiResp.getData() : null;
        } catch (RestClientException ex) {
            log.error("SAP bill post failed for sessionRef={}: {}", sessionRef, ex.getMessage());
            throw new PmkException("Billing service unavailable", "SAP_UNAVAILABLE", ex);
        }
    }

    // Build a JSON HTTP entity with the given body
    private <T> HttpEntity<T> buildRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
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
