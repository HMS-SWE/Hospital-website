package com.example.backend.service;

import com.example.backend.dto.MedicalHistoryResponse;
import com.example.backend.dto.VisitResponse;
import com.example.backend.entity.MedicalRecord;
import com.example.backend.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

        public List<MedicalHistoryResponse> getPatientHistory(Long patientId) {
                List<MedicalRecord> records = medicalRecordRepository.findPatientHistory(patientId);

                return records.stream()
                                .map(record -> MedicalHistoryResponse.builder()
                                                .recordId(record.getId())
                                                .appointmentId(record.getAppointment() != null
                                                                ? record.getAppointment().getId()
                                                                : null)
                                                .condition(record.getDiagnosis())
                                                .treatmentPlan(record.getTreatmentPlan())
                                                .medications(record.getMedications() != null
                                                                ? record.getMedications().stream()
                                                                                .map(m -> m.getName())
                                                                                .collect(Collectors.toList())
                                                                : Collections.emptyList())
                                                .date(record.getCreatedAt() != null
                                                                ? record.getCreatedAt().toLocalDate().toString()
                                                                : null)
                                                .build())
                                .toList();
        }
}