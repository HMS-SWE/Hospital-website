package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class VisitResponse {
    private Long appointmentId;
    private Long patientId;
    private String patientFullName;
    private String appointmentDate;
    private String appointmentTime;
    private String diagnosis;
    private String treatmentPlan;
    private List<String> medications;
}