package com.conversion.pmk.common.enums;

// Supported outbound notification channels
public enum NotificationChannel {

    EMAIL("Email"),
    SMS("SMS");

    private final String label;

    NotificationChannel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
