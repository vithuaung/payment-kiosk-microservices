package com.conversion.pmk.payment.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.payment.dto.request.CardSessionRequest;
import com.conversion.pmk.payment.dto.response.CardSessionResponse;
import com.conversion.pmk.payment.service.CardSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/card")
@Tag(name = "Card Sessions")
@RequiredArgsConstructor
public class CardSessionController {

    private final CardSessionService cardSessionService;

    @PostMapping("/{sessionRef}/open")
    public ApiResponse<CardSessionResponse> openSession(@PathVariable String sessionRef) {
        return ApiResponse.ok(cardSessionService.openSession(sessionRef));
    }

    @PutMapping("/{sessionRef}/update")
    public ApiResponse<CardSessionResponse> updateSession(
            @PathVariable String sessionRef,
            @RequestBody @Valid CardSessionRequest request) {
        return ApiResponse.ok(cardSessionService.updateSession(request));
    }
}
