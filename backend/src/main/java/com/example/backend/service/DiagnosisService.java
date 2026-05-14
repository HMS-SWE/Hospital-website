package com.example.backend.service;

import com.example.backend.dto.diagnosis.*;
import com.example.backend.entity.*;
import com.example.backend.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.enums.AppointmentStatus;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    // ------------------------------------------------------------------ //
    // GET /api/visits/{id} //
    // ------------------------------------------------------------------ //

    @Transactional(readOnly = true)
    public VisitDiagnosisResponse getVisitWithDiagnosis(Long appointmentId, Long doctorId) {
        Appointment appointment = findAppointmentOrThrow(appointmentId);
        assertIsAssignedDoctor(appointment, doctorId);

        MedicalRecord record = medicalRecordRepository
                .findByAppointmentIdWithMedications(appointmentId)
                .orElse(null);

        return toVisitDiagnosisResponse(appointment, record);
    }

    // ------------------------------------------------------------------ //
    // POST /api/visits/{id}/diagnosis //
    // ------------------------------------------------------------------ //

    /**
     * Creates or updates the medical record for the given appointment.
     * The entire operation is a single transaction — Visit update and
     * medication inserts succeed or fail together.
     *
     * Medications are parsed from a newline-separated string matching
     * the UI textarea. Existing medications are replaced wholesale;
     * orphanRemoval on the mapping handles the DELETEs.
     */
    @Transactional
    public DiagnosisResponse saveOrUpdateDiagnosis(Long appointmentId,
            DiagnosisRequest request,
            Long doctorId) {
        Appointment appointment = findAppointmentOrThrow(appointmentId);
        assertIsAssignedDoctor(appointment, doctorId);

        MedicalRecord record = medicalRecordRepository
                .findByAppointmentIdWithMedications(appointmentId)
                .orElseGet(() -> buildNewRecord(appointment));

        record.setDiagnosis(request.diagnosis());
        record.setTreatmentPlan(request.treatmentPlan());
        record.setPrescription(request.prescription());

        // Replace medications — orphanRemoval handles deletion of old rows
        record.getMedications().clear();
        record.getMedications().addAll(parseMedications(request.medications(), record));

        MedicalRecord saved = medicalRecordRepository.save(record);
        Appointment savedAppointment = record.getAppointment();
        savedAppointment.setStatus(AppointmentStatus.COMPLETED);
        return toDiagnosisResponse(saved);
    }

    // ------------------------------------------------------------------ //
    // Utility //
    // ------------------------------------------------------------------ //

    @Transactional(readOnly = true)
    public boolean diagnosisExists(Long appointmentId) {
        return medicalRecordRepository.existsByAppointmentId(appointmentId);
    }

    // ------------------------------------------------------------------ //
    // Private helpers //
    // ------------------------------------------------------------------ //

    /**
     * Splits the raw textarea string by newline and maps each non-blank
     * trimmed line to a Medication entity linked to the given record.
     */
    private List<Medication> parseMedications(String raw, MedicalRecord record) {
        return Arrays.stream(raw.split("\\r?\\n", -1))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(name -> Medication.builder()
                        .medicalRecord(record)
                        .name(name)
                        .build())
                .toList();
    }

    private Appointment findAppointmentOrThrow(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Appointment not found: " + appointmentId));
    }

    private void assertIsAssignedDoctor(Appointment appointment, Long doctorId) {
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new AccessDeniedException(
                    "Only the assigned doctor may access or modify this diagnosis record.");
        }
    }

    private MedicalRecord buildNewRecord(Appointment appointment) {
        return MedicalRecord.builder()
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .startDate(LocalDate.now())
                .build();
    }

    private VisitDiagnosisResponse toVisitDiagnosisResponse(Appointment a, MedicalRecord record) {
        List<MedicationResponse> meds = record == null ? List.of()
                : record.getMedications().stream()
                        .map(m -> new MedicationResponse(m.getId(), m.getName()))
                        .toList();

        return new VisitDiagnosisResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getTimeSlot().getDate(),
                a.getTimeSlot().getStartTime(),
                a.getTimeSlot().getEndTime(),
                record != null ? record.getId() : null,
                record != null ? record.getDiagnosis() : null,
                record != null ? record.getTreatmentPlan() : null,
                record != null ? record.getPrescription() : null,
                meds);
    }

    private DiagnosisResponse toDiagnosisResponse(MedicalRecord record) {
        List<MedicationResponse> meds = record.getMedications().stream()
                .map(m -> new MedicationResponse(m.getId(), m.getName()))
                .toList();

        return new DiagnosisResponse(
                record.getId(),
                record.getAppointment().getId(),
                record.getDiagnosis(),
                record.getTreatmentPlan(),
                record.getPrescription(),
                meds);
    }
}