package com.conversion.pmk.notification.service;

// Outbound email channel
public interface EmailService {

    NotificationResult send(String recipient, String subject, String body);
}
