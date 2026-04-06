package com.conversion.pmk.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Mock email sender — logs output and simulates 5% failure rate
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public NotificationResult send(String recipient, String subject, String body) {
        log.info("EMAIL to={} subject={} body={}", recipient, subject, body);

        try {
            // Simulate SMTP network latency
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5% chance of mock SMTP failure
        if (Math.random() < 0.05) {
            return NotificationResult.failure("Mock SMTP error");
        }

        return NotificationResult.success();
    }
}
