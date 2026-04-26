package com.conversion.pmk.patient.client;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.dto.response.VisitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

// Declarative HTTP client for the NGEMR mock gateway
@FeignClient(name = "ngemr-client", url = "${pmk.mock.ngemr-url}")
public interface NgemrFeignClient {

    @PostMapping("/mock/ngemr/person")
    ApiResponse<PersonResponse> lookupPerson(@RequestBody Map<String, String> body);

    @PostMapping("/mock/ngemr/visits")
    ApiResponse<List<VisitResponse>> lookupVisits(@RequestBody Map<String, String> body);

    @PostMapping("/mock/ngemr/checkin")
    ApiResponse<CheckinResponse> checkinPerson(@RequestBody CheckinRequest request);
}
