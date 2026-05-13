package com.example.backend.controller;

import com.example.backend.dto.diagnosis.DiagnosisRequest;
import com.example.backend.dto.diagnosis.DiagnosisResponse;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.DiagnosisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    /**
     * GET /api/visits/{id}
     *
     * Returns patient info + existing diagnosis/medications for the given
     * appointment.
     * Only the doctor assigned to this visit may call this endpoint.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getVisit(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long doctorId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        if (doctorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        return ResponseEntity.ok(diagnosisService.getVisitWithDiagnosis(id, doctorId));
    }

    /**
     * POST /api/visits/{id}/diagnosis
     *
     * Creates or updates the diagnosis record for the given appointment.
     * Fully transactional — any failure rolls back both Visit update and
     * medication inserts.
     *
     * Returns 201 on first-time creation, 200 on update.
     * Only the assigned doctor may call this endpoint.
     */
    @PostMapping("/{id}/diagnosis")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> saveDiagnosis(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosisRequest request,
            HttpServletRequest httpRequest) {

        Long doctorId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        if (doctorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        boolean existed = diagnosisService.diagnosisExists(id);
        DiagnosisResponse response = diagnosisService.saveOrUpdateDiagnosis(id, request, doctorId);

        return existed
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(201).body(response);
    }
}