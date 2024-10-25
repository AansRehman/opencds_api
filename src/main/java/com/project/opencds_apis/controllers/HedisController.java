package com.project.opencds_apis.controllers;

import com.project.opencds_apis.models.*;
import com.project.opencds_apis.services.HedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/hedis")
public class HedisController {

    @Autowired
    private HedisService hedisService;

    @PostMapping("/evaluate")
    public MeasureResponse evaluate(@RequestBody MeasureRequest request) {
        return hedisService.evaluate(request);
    }

    @GetMapping("/measures/HEDIS_BCS/eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(@RequestBody EligibilityRequest request) {
        EligibilityResponse response = hedisService.checkEligibility(request);
        return ResponseEntity.ok(response);
    }
}
