package com.pm.billingservice.exception;

public class PlanNotDiscountableException extends RuntimeException{
    public PlanNotDiscountableException() {
        super();
    }
    public PlanNotDiscountableException(String message) {
        super(message);
    }
}
