package com.conversion.pmk.common.enums;

// Lifecycle states of a settlement batch
public enum SettlementStatus {

    PENDING("Pending"),
    PROCESSING("Processing"),
    DONE("Done"),
    FAILED("Failed");

    private final String label;

    SettlementStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // True if no further state transitions are expected
    public boolean isTerminal() {
        return this == DONE || this == FAILED;
    }
}
