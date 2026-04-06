package com.conversion.pmk.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Mock SMS sender — logs output and simulates 5% failure rate
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public NotificationResult send(String recipient, String body) {
        log.info("SMS to={} body={}", recipient, body);

        try {
            // Simulate gateway network latency
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5% chance of mock gateway failure
        if (Math.random() < 0.05) {
            return NotificationResult.failure("Mock SMS gateway error");
        }

        return NotificationResult.success();
    }
}
