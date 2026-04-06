package com.conversion.pmk.notification.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// Holds the outcome of a single send attempt
@Data
@AllArgsConstructor
public class NotificationResult {

    private boolean sent;
    private String errorMessage;

    // Build a success result
    public static NotificationResult success() {
        return new NotificationResult(true, null);
    }

    // Build a failure result with the given error message
    public static NotificationResult failure(String msg) {
        return new NotificationResult(false, msg);
    }
}
