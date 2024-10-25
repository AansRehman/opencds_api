package com.project.opencds_apis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EligibilityRequest {
    private String id;
    private String birthDate; // YYYY-MM-DD format
    private String gender;
}
