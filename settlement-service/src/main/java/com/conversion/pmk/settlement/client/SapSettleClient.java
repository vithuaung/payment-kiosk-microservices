package com.conversion.pmk.settlement.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.PmkException;
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

import java.math.BigDecimal;
import java.util.List;

// Sends settlement requests to the mock SAP bill-post endpoint
@Slf4j
@Component
@RequiredArgsConstructor
public class SapSettleClient {

    private final RestTemplate restTemplate;

    @Value("${pmk.mock.sap-url}")
    private String sapUrl;

    public SapSettleResponse settle(SapSettleRequest request) {
        String url = sapUrl + "/mock/sap/bill-post";
        log.debug("Posting to SAP: {} sessionRef={}", url, request.getSessionRef());
        try {
            ResponseEntity<ApiResponse<SapSettleResponse>> response = restTemplate.exchange(
                    url, HttpMethod.POST, buildRequest(request),
                    new ParameterizedTypeReference<ApiResponse<SapSettleResponse>>() {});
            ApiResponse<SapSettleResponse> apiResp = response.getBody();
            return apiResp != null ? apiResp.getData() : null;
        } catch (RestClientException ex) {
            log.error("SAP settle call failed: {}", ex.getMessage());
            throw new PmkException("SAP settle unavailable", "SAP_SETTLE_UNAVAILABLE", ex);
        }
    }

    // Build a JSON HTTP entity with the given body
    private <T> HttpEntity<T> buildRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    // Payload sent to mock SAP
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SapSettleRequest {
        private String sessionRef;
        private String personRef;
        private List<BillItemRef> items;
        private String payMethod;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BillItemRef {
            private String billRef;
            private int billSeq;
            private BigDecimal payableAmt;
            private String orgCode;
        }
    }

    // Response from mock SAP
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SapSettleResponse {
        private String extRef;
        private String postStatus;
        private String message;
    }
}
