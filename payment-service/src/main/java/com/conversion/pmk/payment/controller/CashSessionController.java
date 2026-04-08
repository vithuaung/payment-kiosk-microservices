package com.conversion.pmk.payment.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.payment.dto.request.CashSessionRequest;
import com.conversion.pmk.payment.dto.response.CashSessionResponse;
import com.conversion.pmk.payment.service.CashSessionService;
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
@RequestMapping("/api/payments/cash")
@Tag(name = "Cash Sessions")
@RequiredArgsConstructor
public class CashSessionController {

    private final CashSessionService cashSessionService;

    @PostMapping("/{sessionRef}/open")
    public ApiResponse<CashSessionResponse> openSession(@PathVariable("sessionRef") String sessionRef) {
        return ApiResponse.ok(cashSessionService.openSession(sessionRef));
    }

    @PutMapping("/{sessionRef}/update")
    public ApiResponse<CashSessionResponse> updateSession(
            @PathVariable("sessionRef") String sessionRef,
            @RequestBody @Valid CashSessionRequest request) {
        return ApiResponse.ok(cashSessionService.updateSession(request));
    }

    @PutMapping("/{sessionRef}/close")
    public ApiResponse<CashSessionResponse> closeSession(
            @PathVariable("sessionRef") String sessionRef,
            @RequestBody @Valid CashSessionRequest request) {
        return ApiResponse.ok(cashSessionService.closeSession(
                sessionRef,
                request.getInsertedAmt(),
                request.getReturnedAmt()));
    }
}
