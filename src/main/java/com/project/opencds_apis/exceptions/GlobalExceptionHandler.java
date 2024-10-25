package com.project.opencds_apis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public Map<String, Object> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("details", ex.getDetails());
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @ExceptionHandler(PatientNotEligibleException.class)
    public Map<String, Object> handlePatientNotEligibleException(PatientNotEligibleException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        response.put("error", "Unprocessable Entity");
        response.put("message", ex.getMessage());
        Map<String, String> details = new HashMap<>();
        details.put("reason", ex.getReason());
        response.put("details", details);
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @ExceptionHandler(MeasureNotFoundException.class)
    public Map<String, Object> handleMeasureNotFoundException(MeasureNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}
