package com.example.backend.dto.diagnosis;

import com.example.backend.validation.ValidMedicationLines;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PatientDiagnosisRequest(

        @NotNull(message = "Appointment ID is required") Long appointmentId,

        @NotBlank(message = "Diagnosis cannot be empty") @Size(max = 1000, message = "Diagnosis must not exceed 1000 characters") String diagnosis,

        @NotBlank(message = "Treatment plan cannot be empty") @Size(max = 1000, message = "Treatment plan must not exceed 1000 characters") String treatmentPlan,

        @Size(max = 1000) String prescription,

        @NotBlank(message = "At least one medication entry is required") @Size(max = 5000) @ValidMedicationLines String medications,

        @Size(max = 1000) String notes) {
}