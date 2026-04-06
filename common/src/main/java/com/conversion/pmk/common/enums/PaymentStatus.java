package com.conversion.pmk.common.enums;

// Lifecycle states of a payment transaction
public enum PaymentStatus {

    PENDING("Pending"),
    PROCESSING("Processing"),
    DONE("Done"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // True if no further state transitions are expected
    public boolean isTerminal() {
        return this == DONE || this == FAILED || this == CANCELLED;
    }
}
