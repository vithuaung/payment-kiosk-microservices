package com.conversion.pmk.common.exception;

// Thrown when a requested resource does not exist
public class ResourceNotFoundException extends PmkException {

    public static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName + " not found: " + identifier, ERROR_CODE);
    }
}
