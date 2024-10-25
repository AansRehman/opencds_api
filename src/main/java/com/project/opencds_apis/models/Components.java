package com.project.opencds_apis.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Components {
    private boolean hba1cTest;
    private boolean eyeExam;
    private boolean nephropathy;
    private boolean bpControl;
}