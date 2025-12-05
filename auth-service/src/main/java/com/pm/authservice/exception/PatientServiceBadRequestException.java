package com.pm.authservice.exception;

public class PatientServiceBadRequestException extends PatientServiceException{
    public PatientServiceBadRequestException(String message, int status) {
        super(message, status);
    }
}
