package com.conversion.pmk.notification.service;

import com.conversion.pmk.notification.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

// Core notification operations
public interface NotificationService {

    NotificationResponse sendEmail(UUID paymentId, String recipient, String subject, String body);

    NotificationResponse sendSms(UUID paymentId, String recipient, String body);

    List<NotificationResponse> getByPaymentId(UUID paymentId);
}
