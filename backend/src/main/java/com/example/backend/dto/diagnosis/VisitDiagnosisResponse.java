package com.example.backend.dto.diagnosis;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record VisitDiagnosisResponse(
        Long visitId,
        Long patientId,
        String patientFullName,
        LocalDate appointmentDate,
        LocalTime appointmentStartTime,
        LocalTime appointmentEndTime,

        // null when no diagnosis has been recorded yet
        Long medicalRecordId,
        String diagnosis,
        String treatmentPlan,
        String prescription,
        List<MedicationResponse> medications) {
}