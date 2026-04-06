package com.conversion.pmk.notification.service;

// Outbound SMS channel
public interface SmsService {

    NotificationResult send(String recipient, String body);
}
