package com.conversion.pmk.patient.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Handles patient check-in requests
@Tag(name = "Check-ins", description = "Patient check-in processing")
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "Perform a patient check-in")
    @PostMapping
    public ResponseEntity<ApiResponse<CheckinResponse>> checkin(@Valid @RequestBody CheckinRequest request) {
        CheckinResponse data = checkinService.performCheckin(request);
        return ResponseEntity.ok(ApiResponse.ok("Check-in processed", data));
    }
}
