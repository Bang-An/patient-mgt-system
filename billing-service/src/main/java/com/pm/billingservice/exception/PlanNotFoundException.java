package com.pm.billingservice.exception;

public class PlanNotFoundException extends RuntimeException {
    public PlanNotFoundException() {
        super();
    }
    public PlanNotFoundException(String message) {
        super(message);
    }
}
