package com.project.opencds_apis.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.opencds_apis.exceptions.InvalidRequestException;
import com.project.opencds_apis.exceptions.MeasureNotFoundException;
import com.project.opencds_apis.exceptions.MissingDataException;
import com.project.opencds_apis.exceptions.PatientNotEligibleException;
import com.project.opencds_apis.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HedisService {

    public MeasureResponse evaluate(MeasureRequest request) {

            Map<String, Object> details = new HashMap<>();
            boolean measureMet = false;

            switch (request.getMeasureId()) {
                case "HEDIS_BCS":
                    measureMet = evaluateBreastCancerScreening(request, details);
                    break;
                case "HEDIS_COL":
                    measureMet = evaluateColonoscopyScreening(request, details);
                    break;
                case "HEDIS_CCS":
                    measureMet = evaluateCervicalCancerScreening(request, details);
                    break;
                case "HEDIS_CDC":
                    measureMet = evaluateDiabetesCare(request, details);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Measure '" + request.getMeasureId() + "' not found");
            }

            return new MeasureResponse(request.getMeasureId(), measureMet, details, new String[]{});
          }

    public EligibilityResponse checkEligibility(EligibilityRequest request) {
        validateEligibilityRequest(request);

        EligibilityResponse response = new EligibilityResponse();
        List<String> reasons = new ArrayList<>();

        // Calculate age
        LocalDate birthDate = LocalDate.parse(request.getBirthDate());
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();

        // Check age eligibility
        if (age >= 50 && age <= 74) {
            reasons.add("Patient age (" + age + ") within measure range (50-74)");
        } else {
            reasons.add("Patient age (" + age + ") outside measure range (50-74)");
            response.setEligible(false);
            response.setReasons(reasons);
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, createErrorResponse(422, "Unprocessable Entity", "Patient not eligible for measure",
//                    List.of(new ErrorDetail("age", "Patient age (" + age + ") outside measure range (50-74)"))));
        }

        // Check gender eligibility
        if ("F".equalsIgnoreCase(request.getGender())) {
            reasons.add("Patient gender (" + request.getGender() + ") meets criteria");
        } else {
            reasons.add("Patient gender (" + request.getGender() + ") does not meet criteria");
            response.setEligible(false);
            response.setReasons(reasons);
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, createErrorResponse(422, "Unprocessable Entity", "Patient not eligible for measure",
//                    List.of(new ErrorDetail("gender", "Patient gender (" + request.getGender() + ") does not meet criteria"))));
        }

        // Check for exclusions
        boolean hasExclusions = checkForExclusions(request.getId());
        if (!hasExclusions) {
            reasons.add("No exclusions found");
        } else {
            reasons.add("Exclusions found for the patient");
        }

        // Determine eligibility
        response.setEligible(age >= 50 && age <= 74 && "F".equalsIgnoreCase(request.getGender()) && !hasExclusions);
        response.setReasons(reasons);
        return response;
    }

    private void validateEligibilityRequest(EligibilityRequest request) {
        if (request.getBirthDate() == null || request.getGender() == null) {
            Map<String, String> missingFields = new HashMap<>();
            if (request.getBirthDate() == null) missingFields.put("patient.birthDate", "Birth date is required");
            if (request.getGender() == null) missingFields.put("patient.gender", "Gender is required");
            throw new InvalidRequestException(missingFields);
        }
        if (!checkEligibility(request).isEligible()) {
            LocalDate birthDate = LocalDate.parse(request.getBirthDate());
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            throw new PatientNotEligibleException("Patient age (" + age + ") outside measure range (50-74)");
        }
        if (request.getId().isBlank()) {
            throw new MeasureNotFoundException(request.getId());
        }


        Map<String, String> errors = new HashMap<>();

        if (request.getBirthDate() == null) {
            errors.put("birthDate", "Birth date is required");
        }
        if (request.getGender() == null) {
            errors.put("gender", "Gender is required");
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, createErrorResponse(400, "Bad Request", errors));
        }
    }

    private boolean evaluateDiabetesCare(MeasureRequest request, Map<String, Object> details) {
        boolean hba1cValid = false;
        boolean eyeExamValid = false;
        boolean nephropathyValid = false; // Assuming you check for nephropathy screening
        boolean bpControlValid = false; // Assuming you check for blood pressure control

        String lastHbA1cDate = null;
        String lastEyeExamDate = null;

        if (request.getPatient() != null && request.getPatient().getProcedures() != null) {
            for (MeasureRequest.Procedure procedure : request.getPatient().getProcedures()) {
                switch (procedure.getCode()) {
                    case "83036":  // HbA1c code
                        hba1cValid = true;
                        lastHbA1cDate = procedure.getStartDate();
                        break;
                    case "2022F":  // Eye exam code
                        eyeExamValid = true;
                        lastEyeExamDate = procedure.getStartDate();
                        break;
                    default:
                        break;
                }
            }
        }

        // Check for nephropathy and blood pressure control based on your requirements
        // Here we'll assume they're both false for simplicity, but you can implement checks
        nephropathyValid = false; // Add logic to check for nephropathy screening
        bpControlValid = false; // Add logic to check for blood pressure reading

        // Prepare the components details
        Components components = new Components(hba1cValid, eyeExamValid, nephropathyValid, bpControlValid);
        details.put("components", components);
        details.put("lastHbA1cDate", lastHbA1cDate);
        details.put("lastEyeExamDate", lastEyeExamDate);

        // Collect messages based on missing screenings
        String[] messages = new String[] {};
        if (!nephropathyValid) {
            messages = addMessage(messages, "Missing nephropathy screening");
        }
        if (!bpControlValid) {
            messages = addMessage(messages, "Missing blood pressure reading");
        }

        details.put("messages", messages);

        // Measure is met if both HbA1c test and eye exam are valid
        return hba1cValid && eyeExamValid;
    }

    private String[] addMessage(String[] messages, String message) {
        String[] newMessages = new String[messages.length + 1];
        System.arraycopy(messages, 0, newMessages, 0, messages.length);
        newMessages[messages.length] = message;
        return newMessages;
    }


    private boolean evaluateBreastCancerScreening(MeasureRequest request, Map<String, Object> details) {
        boolean measureMet = false;
        String lastMammogramDate = null;
        int mammogramCount = 0;

        if (request.getPatient() != null && request.getPatient().getProcedures() != null) {
            for (MeasureRequest.Procedure procedure : request.getPatient().getProcedures()) {
                if ("77067".equals(procedure.getCode())) {  // Mammogram code
                    measureMet = true;
                    lastMammogramDate = procedure.getStartDate();
                    mammogramCount++;
                }
            }
        }

        details.put("mammogramCount", String.valueOf(mammogramCount));
        details.put("lastMammogramDate", lastMammogramDate != null ? lastMammogramDate : "N/A");
        details.put("hasExclusions", "false");

        return measureMet;
    }

    private boolean evaluateColonoscopyScreening(MeasureRequest request, Map<String, Object> details) {
        boolean measureMet = false;
        String lastColonoscopyDate = null;
        boolean colonoscopyValid = false;
        boolean fobtValid = false;

        if (request.getPatient() != null && request.getPatient().getProcedures() != null) {
            for (MeasureRequest.Procedure procedure : request.getPatient().getProcedures()) {
                if ("45378".equals(procedure.getCode())) {  // Colonoscopy code
                    colonoscopyValid = true;
                    lastColonoscopyDate = procedure.getStartDate();
                    measureMet = true;
                }
            }
        }

        Map<String, ScreeningDetail> screenings = new HashMap<>();
        screenings.put("COLONOSCOPY", new ScreeningDetail("COLONOSCOPY", colonoscopyValid, lastColonoscopyDate, 120));
        screenings.put("FOBT", new ScreeningDetail("FOBT", fobtValid, null, 12));

        details.put("screenings", screenings);
        return measureMet;
    }

    private boolean evaluateCervicalCancerScreening(MeasureRequest request, Map<String, Object> details) {
        boolean cytologyValid = false;
        boolean hpvValid = false;
        String lastCytologyDate = null;
        String lastHpvDate = null;

        if (request.getPatient() != null && request.getPatient().getProcedures() != null) {
            for (MeasureRequest.Procedure procedure : request.getPatient().getProcedures()) {
                switch (procedure.getCode()) {
                    case "88142":  // Cytology code
                        cytologyValid = true;
                        lastCytologyDate = procedure.getStartDate();
                        break;
                    case "87624":  // HPV Test code
                        hpvValid = true;
                        lastHpvDate = procedure.getStartDate();
                        break;
                    default:
                        break;
                }
            }
        }

        // Create ScreeningResult entries for both screenings
        Map<String, ScreeningResult> screenings = new HashMap<>();
        screenings.put("cytologyResult", new ScreeningResult("CYTOLOGY", cytologyValid, lastCytologyDate, 36));
        screenings.put("hpvResult", new ScreeningResult("HPV", hpvValid, lastHpvDate, 60));

        // Determine if co-testing is valid
        boolean coTestingValid = cytologyValid && hpvValid;

        // Add the screenings to details
        details.put("screenings", screenings);
        details.put("coTestingValid", coTestingValid);

        // Measure is met if either cytology or HPV screening is valid
        return cytologyValid || hpvValid;
    }

//    public EligibilityResponse checkEligibility(EligibilityRequest request) {
//        EligibilityResponse response = new EligibilityResponse();
//        List<String> reasons = new ArrayList<>();
//
//        // Calculate age
//        LocalDate birthDate = LocalDate.parse(request.getBirthDate());
//        LocalDate currentDate = LocalDate.now();
//        int age = Period.between(birthDate, currentDate).getYears();
//
//        // Check age eligibility
//        if (age >= 50 && age <= 74) {
//            reasons.add("Patient age (" + age + ") within measure range (50-74)");
//        } else {
//            reasons.add("Patient age (" + age + ") outside measure range (50-74)");
//        }
//
//        // Check gender eligibility
//        if ("F".equalsIgnoreCase(request.getGender())) {
//            reasons.add("Patient gender (" + request.getGender() + ") meets criteria");
//        } else {
//            reasons.add("Patient gender (" + request.getGender() + ") does not meet criteria");
//        }
//
//        // Check for exclusions (this can be customized based on your requirements)
//        boolean hasExclusions = checkForExclusions(request.getId());
//        if (!hasExclusions) {
//            reasons.add("No exclusions found");
//        } else {
//            reasons.add("Exclusions found for the patient");
//        }
//
//        // Determine eligibility
//        response.setEligible(age >= 50 && age <= 74 && "F".equalsIgnoreCase(request.getGender()) && !hasExclusions);
//        response.setReasons(reasons);
//        return response;
//    }

    private boolean checkForExclusions(String patientId) {
        // Implement your logic to check for exclusions
        // This is a placeholder implementation. Replace it with actual logic.
        return false; // Assuming no exclusions by default
    }


    private String createErrorResponse(int status, String title, Map<String, String> errors) {
        return String.format("{\"status\": %d, \"title\": \"%s\", \"errors\": %s}", status, title, errors.toString());
    }

    private String createErrorResponse(int status, String title, String detail, List<ErrorDetail> errorDetails) {
        StringBuilder details = new StringBuilder();
        for (ErrorDetail errorDetail : errorDetails) {
            details.append(String.format("{\"field\": \"%s\", \"message\": \"%s\"},", errorDetail.getField(), errorDetail.getMessage()));
        }
        if (details.length() > 0) {
            details.deleteCharAt(details.length() - 1); // Remove last comma
        }
        return String.format("{\"status\": %d, \"title\": \"%s\", \"detail\": \"%s\", \"errors\": [%s]}", status, title, detail, details.toString());
    }

}
