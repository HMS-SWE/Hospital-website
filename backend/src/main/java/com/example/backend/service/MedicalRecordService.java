package com.example.backend.service;

import com.example.backend.dto.ActiveMedicationResponse;
import com.example.backend.dto.MedicalHistoryResponse;
import com.example.backend.dto.VisitResponse;
import com.example.backend.dto.diagnosis.DiagnosisResponse;
import com.example.backend.dto.diagnosis.MedicationResponse;
import com.example.backend.dto.diagnosis.PatientDiagnosisRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.MedicalRecord;
import com.example.backend.entity.Medication;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.MedicalRecordRepository;
import java.util.Arrays;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

        private final MedicalRecordRepository medicalRecordRepository;
        private final AppointmentRepository appointmentRepository;

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

        public List<ActiveMedicationResponse> getActiveMedications(Long patientId) {
                List<MedicalRecord> records = medicalRecordRepository
                                .findLatestRecordWithMedications(patientId);

                if (records.isEmpty()) {
                        return Collections.emptyList();
                }

                MedicalRecord latest = records.get(0);

                if (latest.getMedications() == null || latest.getMedications().isEmpty()) {
                        return Collections.emptyList();
                }

                return latest.getMedications().stream()
                                .map(m -> ActiveMedicationResponse.builder()
                                                .medicationId(m.getId())
                                                .name(m.getName())
                                                .diagnosisDate(latest.getCreatedAt() != null
                                                                ? latest.getCreatedAt().toLocalDate().toString()
                                                                : null)
                                                .condition(latest.getDiagnosis())
                                                .build())
                                .toList();
        }

        @Transactional
        public DiagnosisResponse createDiagnosis(Long patientId,
                        PatientDiagnosisRequest request,
                        Long doctorId) throws AccessDeniedException {

                // Verify appointment exists and belongs to patient
                Appointment appointment = appointmentRepository.findById(request.appointmentId())
                                .orElseThrow(() -> new RuntimeException("Appointment not found"));

                if (!appointment.getPatient().getId().equals(patientId)) {
                        throw new RuntimeException("Appointment does not belong to this patient");
                }

                // Only the assigned doctor can create diagnosis
                if (!appointment.getDoctor().getId().equals(doctorId)) {
                        throw new AccessDeniedException("Only the assigned doctor may create a diagnosis record");
                }
                if (!appointmentRepository.existsByDoctorIdAndPatientId(doctorId, patientId)) {
                        throw new AccessDeniedException(
                                        "Only doctors assigned to this patient may create diagnosis records");
                }
                // Immutable — no updates allowed
                if (medicalRecordRepository.existsByAppointmentId(request.appointmentId())) {
                        throw new RuntimeException("Diagnosis already exists for this appointment");
                }

                // Build and save medical record
                MedicalRecord record = MedicalRecord.builder()
                                .appointment(appointment)
                                .doctor(appointment.getDoctor())
                                .patient(appointment.getPatient())
                                .diagnosis(request.diagnosis())
                                .treatmentPlan(request.treatmentPlan())
                                .prescription(request.prescription())
                                .notes(request.notes())
                                .startDate(LocalDate.now())
                                .build();

                // Parse and attach medications
                List<Medication> medications = Arrays.stream(request.medications().split("\\r?\\n", -1))
                                .map(String::trim)
                                .filter(line -> !line.isBlank())
                                .map(name -> Medication.builder()
                                                .medicalRecord(record)
                                                .name(name)
                                                .build())
                                .toList();

                record.getMedications().addAll(medications);

                MedicalRecord saved = medicalRecordRepository.save(record);

                List<MedicationResponse> medicationResponses = saved.getMedications().stream()
                                .map(m -> new MedicationResponse(m.getId(), m.getName()))
                                .toList();

                return new DiagnosisResponse(
                                saved.getId(),
                                saved.getAppointment().getId(),
                                saved.getDiagnosis(),
                                saved.getTreatmentPlan(),
                                saved.getPrescription(),
                                medicationResponses);
        }
}