package com.pm.billingservice.exception;

public class DiscountInactiveException extends RuntimeException {
    public DiscountInactiveException() {
        super();
    }
    public DiscountInactiveException(String message) {
        super(message);
    }
}
