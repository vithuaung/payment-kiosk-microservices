package com.conversion.pmk.common.enums;

// States of a patient visit
public enum VisitStatus {

    SCHEDULED("Scheduled"),
    ARRIVED("Arrived"),
    COMPLETED("Completed"),
    MISSED("Missed"),
    CANCELLED("Cancelled");

    private final String label;

    VisitStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
