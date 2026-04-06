package com.conversion.pmk.patient.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Handles patient identity lookup and registration
@Tag(name = "Patients", description = "Patient lookup and registration")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @Operation(summary = "Look up a patient by identity reference")
    @PostMapping("/lookup")
    public ResponseEntity<ApiResponse<PersonResponse>> lookup(@Valid @RequestBody PersonLookupRequest request) {
        PersonResponse data = personService.findByIdRef(request.getIdRef());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @Operation(summary = "Register a patient from NGEMR")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PersonResponse>> register(@Valid @RequestBody PersonLookupRequest request) {
        PersonResponse data = personService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Patient registered", data));
    }
}
