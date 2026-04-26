package com.conversion.pmk.settlement.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.PmkException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// Thin wrapper around SapSettleFeignClient — handles ApiResponse unwrapping,
// circuit breaking, and retry with fallback on failure.
@Slf4j
@Component
@RequiredArgsConstructor
public class SapSettleClient {

    private final SapSettleFeignClient feignClient;

    @CircuitBreaker(name = "sap-settle", fallbackMethod = "settleFallback")
    @Retry(name = "sap-settle")
    public SapSettleResponse settle(SapSettleRequest request) {
        log.debug("Posting to SAP settle: sessionRef={}", request.getSessionRef());
        ApiResponse<SapSettleResponse> resp = feignClient.settle(request);
        return resp != null ? resp.getData() : null;
    }

    // ─── Fallback ─────────────────────────────────────────────────────────────

    SapSettleResponse settleFallback(SapSettleRequest request, Exception ex) {
        log.error("SAP settle unavailable for sessionRef={}: {}", request.getSessionRef(), ex.getMessage());
        throw new PmkException("SAP settle unavailable", "SAP_SETTLE_UNAVAILABLE", ex);
    }

    // ─── Request / Response DTOs ──────────────────────────────────────────────

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
