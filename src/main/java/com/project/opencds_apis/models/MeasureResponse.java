package com.project.opencds_apis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasureResponse {
    private String measureId;
    private boolean measureMet;
    private Map<String, Object> details;
    private String[] messages;


    // Constructors, Getters, and Setters
}