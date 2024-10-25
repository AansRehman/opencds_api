package com.project.opencds_apis.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse2 {
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;
    private Instant timestamp;

    public ErrorResponse2(int status, String error, String message, Map<String, String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = Instant.now();
    }
}
