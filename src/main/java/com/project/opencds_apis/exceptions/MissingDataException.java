package com.project.opencds_apis.exceptions;

import java.util.Map;

public class MissingDataException extends Exception {
    private final Map<String, String> details;

    public MissingDataException(Map<String, String> details) {
        super("Missing required data");
        this.details = details;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
