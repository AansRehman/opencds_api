package com.project.opencds_apis.exceptions;

import java.util.Map;

// InvalidRequestException.java
public class InvalidRequestException extends RuntimeException {
    private Map<String, String> details;

    public InvalidRequestException(Map<String, String> details) {
        super("Invalid Request - Missing Required Data");
        this.details = details;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}