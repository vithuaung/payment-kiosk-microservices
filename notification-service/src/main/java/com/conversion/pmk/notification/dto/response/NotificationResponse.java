package com.conversion.pmk.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Response payload returned by the notification query endpoint
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID notifId;
    private String paymentId;
    private String notifChannel;
    private String recipient;
    private String subject;
    private String sendStatus;
    private String sentAt;
    private String failReason;
    private String createdAt;
}
