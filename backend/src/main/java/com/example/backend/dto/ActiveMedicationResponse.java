package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActiveMedicationResponse {
    private Long medicationId;
    private String name;
    private String diagnosisDate;
    private String condition;
}