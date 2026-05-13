package com.example.backend.service;

import com.example.backend.dto.VisitResponse;
import com.example.backend.entity.MedicalRecord;
import com.example.backend.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public VisitResponse getVisitByAppointmentId(Long appointmentId) {
        MedicalRecord record = medicalRecordRepository
                .findByAppointmentIdWithDetails(appointmentId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        return VisitResponse.builder()
                .appointmentId(record.getAppointment().getId())
                .patientId(record.getPatient().getId())
                .patientFullName(record.getPatient().getFullName())
                .appointmentDate(record.getAppointment().getTimeSlot().getDate().toString())
                .appointmentTime(record.getAppointment().getTimeSlot().getStartTime().toString())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .medications(record.getMedications() != null
                        ? record.getMedications().stream()
                                .map(m -> m.getName())
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }
}