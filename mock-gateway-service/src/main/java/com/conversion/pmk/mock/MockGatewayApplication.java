package com.conversion.pmk.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Entry point for the mock external-systems gateway
@SpringBootApplication
public class MockGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockGatewayApplication.class, args);
    }
}
