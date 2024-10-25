package com.project.opencds_apis.exceptions;


// MeasureNotFoundException.java
public class MeasureNotFoundException extends RuntimeException {
    public MeasureNotFoundException(String measureId) {
        super("Measure '" + measureId + "' not found");
    }
}
