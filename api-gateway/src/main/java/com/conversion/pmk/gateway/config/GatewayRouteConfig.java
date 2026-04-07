package com.conversion.pmk.gateway.config;

import com.conversion.pmk.gateway.filter.JwtAuthGatewayFilter;
import com.conversion.pmk.gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Defines all gateway routes and attaches the JWT filter where required
@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final JwtUtil jwtUtil;

    @Value("${pmk.patient-service.url}")
    private String patientServiceUrl;

    @Value("${pmk.payment-service.url}")
    private String paymentServiceUrl;

    @Value("${pmk.settlement-service.url}")
    private String settlementServiceUrl;

    @Value("${pmk.notification-service.url}")
    private String notificationServiceUrl;

    @Value("${pmk.mock-gateway.url}")
    private String mockGatewayUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        JwtAuthGatewayFilter jwtFilter = new JwtAuthGatewayFilter(jwtUtil);

        return builder.routes()

                // --- patient-service ---
                .route("patient-patients", r -> r
                        .path("/api/patients/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(patientServiceUrl))

                .route("patient-visits", r -> r
                        .path("/api/visits/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(patientServiceUrl))

                .route("patient-checkins", r -> r
                        .path("/api/checkins/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(patientServiceUrl))

                // --- payment-service ---
                .route("payment-bills", r -> r
                        .path("/api/bills/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(paymentServiceUrl))

                .route("payment-payments", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(paymentServiceUrl))

                // --- settlement-service ---
                .route("settlement-settlements", r -> r
                        .path("/api/settlements/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(settlementServiceUrl))

                // --- notification-service ---
                .route("notification-notifications", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(notificationServiceUrl))

                // --- mock-gateway-service (no auth) ---
                .route("mock-gateway", r -> r
                        .path("/mock/**")
                        .uri(mockGatewayUrl))

                // /api/auth/** is handled locally by AuthController — no upstream route needed

                .build();
    }
}
