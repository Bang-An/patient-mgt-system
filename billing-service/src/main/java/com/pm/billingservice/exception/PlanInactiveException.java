package com.pm.billingservice.exception;

public class PlanInactiveException extends RuntimeException{
    public PlanInactiveException() {
        super();
    }
    public PlanInactiveException(String message) {
        super(message);
    }
}
