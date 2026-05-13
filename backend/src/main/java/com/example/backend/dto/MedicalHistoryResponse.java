package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MedicalHistoryResponse {
    private Long recordId;
    private Long appointmentId;
    private String condition;
    private String treatmentPlan;
    private List<String> medications;
    private String date;
}