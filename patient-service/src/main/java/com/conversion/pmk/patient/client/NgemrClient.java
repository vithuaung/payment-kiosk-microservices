package com.conversion.pmk.patient.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.dto.response.VisitResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// Thin wrapper around NgemrFeignClient — handles ApiResponse unwrapping,
// circuit breaking, and retry with fallback on failure.
@Slf4j
@Component
@RequiredArgsConstructor
public class NgemrClient {

    private final NgemrFeignClient feignClient;

    @CircuitBreaker(name = "ngemr", fallbackMethod = "lookupPersonFallback")
    @Retry(name = "ngemr")
    public PersonResponse lookupPerson(String idRef) {
        ApiResponse<PersonResponse> resp = feignClient.lookupPerson(Map.of("idRef", idRef));
        return resp != null ? resp.getData() : null;
    }

    @CircuitBreaker(name = "ngemr", fallbackMethod = "lookupVisitsFallback")
    @Retry(name = "ngemr")
    public List<VisitResponse> lookupVisits(String idRef) {
        ApiResponse<List<VisitResponse>> resp = feignClient.lookupVisits(Map.of("idRef", idRef));
        return resp != null ? resp.getData() : null;
    }

    @CircuitBreaker(name = "ngemr", fallbackMethod = "checkinPersonFallback")
    @Retry(name = "ngemr")
    public CheckinResponse checkinPerson(CheckinRequest req) {
        ApiResponse<CheckinResponse> resp = feignClient.checkinPerson(req);
        return resp != null ? resp.getData() : null;
    }

    // ─── Fallbacks (circuit open or all retries exhausted) ────────────────────

    PersonResponse lookupPersonFallback(String idRef, Exception ex) {
        log.error("NGEMR person lookup unavailable for idRef={}: {}", idRef, ex.getMessage());
        throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
    }

    List<VisitResponse> lookupVisitsFallback(String idRef, Exception ex) {
        log.error("NGEMR visit lookup unavailable for idRef={}: {}", idRef, ex.getMessage());
        throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
    }

    CheckinResponse checkinPersonFallback(CheckinRequest req, Exception ex) {
        log.error("NGEMR checkin unavailable for idRef={}: {}", req.getIdRef(), ex.getMessage());
        throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
    }
}
