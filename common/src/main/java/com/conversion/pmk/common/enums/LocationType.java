package com.conversion.pmk.common.enums;

// Hierarchy level of a physical or logical location
public enum LocationType {

    ORG("Organisation"),
    BRANCH("Branch"),
    COUNTER("Counter");

    private final String label;

    LocationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
