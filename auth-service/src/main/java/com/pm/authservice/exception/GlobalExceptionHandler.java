package com.pm.authservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(PatientServiceBadRequestException.class)
    public ResponseEntity<String> handlePatientServiceBadRequestException(PatientServiceException ex) {
        log.warn("PatientServiceBadRequestException collected at GlobalExceptionHandler");
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(PatientServiceException.class)
    public ResponseEntity<String> handlePatientServiceException(PatientServiceException ex) {
        log.warn("PatientServiceException collected at GlobalExceptionHandler");
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.warn("GeneralException collected at GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
    }
}
