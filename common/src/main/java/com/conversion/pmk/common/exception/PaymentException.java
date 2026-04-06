package com.conversion.pmk.common.exception;

// Thrown when a payment operation fails
public class PaymentException extends PmkException {

    public PaymentException(String message, String errorCode) {
        super(message, errorCode);
    }
}
