package com.example.backend.controller;

import com.example.backend.dto.ActiveMedicationResponse;
import com.example.backend.dto.MedicalHistoryResponse;
import com.example.backend.dto.diagnosis.DiagnosisResponse;
import com.example.backend.dto.diagnosis.PatientDiagnosisRequest;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.MedicalRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getPatientHistory(@PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        Role authenticatedRole = (Role) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ROLE);

        if (authenticatedUserId == null || authenticatedRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        // Patient can only view their own history
        if (authenticatedRole == Role.PATIENT && !authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Forbidden\"}");
        }

        try {
            List<MedicalHistoryResponse> history = medicalRecordService.getPatientHistory(id);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/medications/active")
    public ResponseEntity<?> getActiveMedications(@PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        Role authenticatedRole = (Role) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ROLE);

        if (authenticatedUserId == null || authenticatedRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        if (authenticatedRole == Role.PATIENT && !authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Forbidden\"}");
        }

        try {
            List<ActiveMedicationResponse> medications = medicalRecordService
                    .getActiveMedications(id);
            return ResponseEntity.ok(medications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // SCRUM-150: Create diagnosis for patient
    @PostMapping("/{id}/diagnoses")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<?> createDiagnosis(@PathVariable Long id,
            @Valid @RequestBody PatientDiagnosisRequest request,
            HttpServletRequest httpRequest) throws java.nio.file.AccessDeniedException {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);

        if (authenticatedUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        try {
            DiagnosisResponse response = medicalRecordService.createDiagnosis(
                    id, request, authenticatedUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Forbidden\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}