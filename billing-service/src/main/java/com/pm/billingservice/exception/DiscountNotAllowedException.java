package com.pm.billingservice.exception;

public class DiscountNotAllowedException extends RuntimeException{
    public DiscountNotAllowedException() {
        super();
    }
    public DiscountNotAllowedException(String message) {
        super(message);
    }
}
