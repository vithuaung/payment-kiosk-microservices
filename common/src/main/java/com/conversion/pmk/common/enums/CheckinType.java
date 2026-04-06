package com.conversion.pmk.common.enums;

// How a patient checked in at the kiosk
public enum CheckinType {

    APPOINTMENT("Appointment"),
    WALKIN("Walk-in");

    private final String label;

    CheckinType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
