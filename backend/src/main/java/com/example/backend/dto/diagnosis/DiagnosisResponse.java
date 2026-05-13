package com.example.backend.dto.diagnosis;

import java.util.List;

public record DiagnosisResponse(
        Long medicalRecordId,
        Long visitId,
        String diagnosis,
        String treatmentPlan,
        String prescription,
        List<MedicationResponse> medications) {
}