package com.conversion.pmk.gateway.config;

import com.conversion.pmk.gateway.filter.JwtAuthGatewayFilter;
import com.conversion.pmk.gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Defines all gateway routes and attaches the JWT filter where required
@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        JwtAuthGatewayFilter jwtFilter = new JwtAuthGatewayFilter(jwtUtil);

        return builder.routes()

                // --- patient-service ---
                .route("patient-patients", r -> r
                        .path("/api/patients/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://patient-service"))

                .route("patient-visits", r -> r
                        .path("/api/visits/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://patient-service"))

                .route("patient-checkins", r -> r
                        .path("/api/checkins/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://patient-service"))

                // --- payment-service ---
                .route("payment-bills", r -> r
                        .path("/api/bills/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://payment-service"))

                .route("payment-payments", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://payment-service"))

                // --- settlement-service ---
                .route("settlement-settlements", r -> r
                        .path("/api/settlements/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://settlement-service"))

                // --- notification-service ---
                .route("notification-notifications", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://notification-service"))

                // --- mock-gateway-service (no auth) ---
                .route("mock-gateway", r -> r
                        .path("/mock/**")
                        .uri("lb://mock-gateway-service"))

                // --- auth handled locally by AuthController (no upstream route needed) ---
                .route("auth-local", r -> r
                        .path("/api/auth/**")
                        .uri("no://op"))

                .build();
    }
}
