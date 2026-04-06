package com.conversion.pmk.patient.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.patient.dto.request.VisitListRequest;
import com.conversion.pmk.patient.dto.response.VisitResponse;
import com.conversion.pmk.patient.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handles retrieval of patient visit records
@Tag(name = "Visits", description = "Patient visit listing")
@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @Operation(summary = "List visits for a patient with optional status filter")
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> listVisits(@RequestBody VisitListRequest request) {
        List<VisitResponse> data = visitService.getVisits(request.getIdRef(), request.getVisitStatus());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
