package com.conversion.pmk.gateway.filter;

import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

// Per-route filter that validates the Bearer JWT and forwards identity headers
@Slf4j
@RequiredArgsConstructor
public class JwtAuthGatewayFilter implements GatewayFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                    "{\"success\":false,\"message\":\"Missing token\",\"errorCode\":\"AUTH_MISSING_TOKEN\"}");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        Claims claims;
        try {
            claims = jwtUtil.validateToken(token);
        } catch (PmkException ex) {
            log.debug("Rejected request — {}", ex.getMessage());
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                    "{\"success\":false,\"message\":\"Invalid token\",\"errorCode\":\"AUTH_INVALID_TOKEN\"}");
        }

        // Forward terminal identity to downstream services
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Terminal-Code", claims.getSubject())
                .header("X-User-Role", claims.get("role", String.class))
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    // Write a JSON error body and complete the response
    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
