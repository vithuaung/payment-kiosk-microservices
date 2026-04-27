package com.conversion.pmk.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    @Value("${pmk.mock-gateway.url}")
    private String mockGatewayUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                // --- patient-service ---
                .route("patient-patients", r -> r
                        .path("/api/patients/**")
                        .uri("lb://patient-service"))

                .route("patient-visits", r -> r
                        .path("/api/visits/**")
                        .uri("lb://patient-service"))

                .route("patient-checkins", r -> r
                        .path("/api/checkins/**")
                        .uri("lb://patient-service"))

                // --- payment-service ---
                .route("payment-bills", r -> r
                        .path("/api/bills/**")
                        .uri("lb://payment-service"))

                .route("payment-payments", r -> r
                        .path("/api/payments/**")
                        .uri("lb://payment-service"))

                // --- settlement-service ---
                .route("settlement-settlements", r -> r
                        .path("/api/settlements/**")
                        .uri("lb://settlement-service"))

                // --- notification-service ---
                .route("notification-notifications", r -> r
                        .path("/api/notifications/**")
                        .uri("lb://notification-service"))

                // --- mock-gateway-service (no auth) ---
                .route("mock-gateway", r -> r
                        .path("/mock/**")
                        .uri(mockGatewayUrl))

                .build();
    }
}
