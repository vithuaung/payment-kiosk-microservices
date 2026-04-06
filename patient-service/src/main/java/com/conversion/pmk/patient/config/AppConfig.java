package com.conversion.pmk.patient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.client.RestTemplate;

// Application-level beans and Kafka enablement
@EnableKafka
@Configuration
public class AppConfig {

    // Shared HTTP client for outbound REST calls
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
