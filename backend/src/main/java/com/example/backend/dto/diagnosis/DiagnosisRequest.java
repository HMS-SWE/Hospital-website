package com.example.backend.dto.diagnosis;

import com.example.backend.validation.ValidMedicationLines;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiagnosisRequest(

        @NotBlank(message = "Diagnosis cannot be empty")
        @Size(max = 1000, message = "Diagnosis must not exceed 1000 characters")
        String diagnosis,

        @NotBlank(message = "Treatment plan cannot be empty")
        @Size(max = 1000, message = "Treatment plan must not exceed 1000 characters")
        String treatmentPlan,

        @Size(max = 1000)
        String prescription,

        /**
         * One medication per line, matching the textarea in the UI.
         * Example payload:
         *   "Paracetamol 500mg\nAmoxicillin 250mg\nIbuprofen 400mg"
         *
         * Validation rules:
         *  - At least one line required (@NotBlank)
         *  - No blank/empty lines allowed (@ValidMedicationLines)
         */
        @NotBlank(message = "At least one medication entry is required")
        @Size(max = 5000)
        @ValidMedicationLines
        String medications
) {}