package com.project.opencds_apis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasureRequest {
    private String measureId;
    private String version;
    private String evaluationDate;
    private Patient patient;

    // Getters and Setters

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Patient {
        private String id;
        private String birthDate;
        private String gender;
        private List<Procedure> procedures;

        // Getters and Setters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Procedure {
        private String id;
        private String code;
        private String codeSystem;
        private String displayName;
        private String startDate;
        private String endDate;

        // Getters and Setters
    }
}