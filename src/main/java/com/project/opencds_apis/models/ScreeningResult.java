package com.project.opencds_apis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningResult {
    private String screeningType;
    private boolean valid;
    private String lastScreeningDate;
    private int lookbackMonths;
}
