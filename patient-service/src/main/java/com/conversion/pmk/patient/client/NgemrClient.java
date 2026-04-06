package com.conversion.pmk.patient.client;

import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.dto.response.VisitResponse;
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

// HTTP client for the NGEMR mock gateway
@Slf4j
@Component
@RequiredArgsConstructor
public class NgemrClient {

    private final RestTemplate restTemplate;

    @Value("${pmk.mock.ngemr-url}")
    private String ngemrUrl;

    // Look up a patient record from NGEMR by identity reference
    public PersonResponse lookupPerson(String idRef) {
        try {
            String url = ngemrUrl + "/mock/ngemr/person";
            Map<String, String> body = Map.of("idRef", idRef);
            ResponseEntity<PersonResponse> response = restTemplate.postForEntity(url, buildRequest(body), PersonResponse.class);
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("NGEMR person lookup failed for idRef={}: {}", idRef, ex.getMessage());
            throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
        }
    }

    // Retrieve visit list for a patient from NGEMR
    public List<VisitResponse> lookupVisits(String idRef) {
        try {
            String url = ngemrUrl + "/mock/ngemr/visits";
            Map<String, String> body = Map.of("idRef", idRef);
            ResponseEntity<List<VisitResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    buildRequest(body),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("NGEMR visit lookup failed for idRef={}: {}", idRef, ex.getMessage());
            throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
        }
    }

    // Submit a check-in to NGEMR and receive the confirmed response
    public CheckinResponse checkinPerson(CheckinRequest req) {
        try {
            String url = ngemrUrl + "/mock/ngemr/checkin";
            ResponseEntity<CheckinResponse> response = restTemplate.postForEntity(url, buildRequest(req), CheckinResponse.class);
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("NGEMR checkin failed for idRef={}: {}", req.getIdRef(), ex.getMessage());
            throw new PmkException("EMR service unavailable", "NGEMR_UNAVAILABLE", ex);
        }
    }

    // Build a JSON HTTP entity with the given body
    private <T> HttpEntity<T> buildRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
