package com.conversion.pmk.common.exception;

// Base runtime exception for all PMK application errors
public class PmkException extends RuntimeException {

    private final String errorCode;

    public PmkException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PmkException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
