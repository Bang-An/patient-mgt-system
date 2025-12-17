package com.pm.billingservice.exception;

public class AccountNotEligibleToInvoiceException extends RuntimeException {
    public AccountNotEligibleToInvoiceException() {
        super();
    }
    public AccountNotEligibleToInvoiceException(String message) {
        super(message);
    }
}
