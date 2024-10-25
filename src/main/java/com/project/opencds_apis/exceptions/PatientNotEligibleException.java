package com.project.opencds_apis.exceptions;

// PatientNotEligibleException.java
public class PatientNotEligibleException extends RuntimeException {
    private String reason;

    public PatientNotEligibleException(String reason) {
        super("Patient not eligible for measure");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
