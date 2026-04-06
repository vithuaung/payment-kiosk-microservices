package com.conversion.pmk.common.enums;

// States of a kiosk or terminal session
public enum SessionStatus {

    OPEN("Open"),
    CLOSED("Closed"),
    ERROR("Error"),
    APPROVED("Approved"),
    DECLINED("Declined"),
    TIMEOUT("Timeout");

    private final String label;

    SessionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
