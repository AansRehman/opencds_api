package com.project.opencds_apis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HedisCdcDetails {
    private Components components;
    private String lastHbA1cDate;
    private String lastEyeExamDate;
}
