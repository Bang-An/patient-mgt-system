package com.pm.authservice.exception;

public class PatientServiceException extends RuntimeException{


    private int statusCode;
    public PatientServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public PatientServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
