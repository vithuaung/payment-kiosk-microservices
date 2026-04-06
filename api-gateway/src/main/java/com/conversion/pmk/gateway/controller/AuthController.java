package com.conversion.pmk.gateway.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.gateway.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// Issues JWT tokens in exchange for a valid API key
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @Value("${pmk.auth.api-key}")
    private String expectedApiKey;

    @PostMapping("/token")
    public Mono<ResponseEntity<ApiResponse<TokenResponse>>> token(@RequestBody TokenRequest request) {
        if (!expectedApiKey.equals(request.getApiKey())) {
            return Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("Invalid API key", "AUTH_INVALID_API_KEY")));
        }

        long expiresAt = System.currentTimeMillis() + 3_600_000L;
        String jwt = jwtUtil.generateToken(request.getTerminalCode(), "TERMINAL");
        TokenResponse body = new TokenResponse(jwt, expiresAt);

        return Mono.just(ResponseEntity.ok(ApiResponse.ok(body)));
    }

    // --- inner DTOs ---

    @Data
    public static class TokenRequest {
        private String apiKey;
        private String terminalCode;
    }

    @Data
    @RequiredArgsConstructor
    public static class TokenResponse {
        private final String token;
        private final long expiresAt;
    }
}
