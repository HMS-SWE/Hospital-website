package com.example.backend.service;

import com.example.backend.dto.ActiveMedicationResponse;
import com.example.backend.dto.MedicalHistoryResponse;
import com.example.backend.dto.diagnosis.DiagnosisResponse;
import com.example.backend.dto.diagnosis.PatientDiagnosisRequest;
import com.example.backend.entity.*;
import com.example.backend.enums.AppointmentStatus;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.MedicalRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Doctor makeDoctor(Long id) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setFullName("Dr. Test");
        return doctor;
    }

    private Patient makePatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFullName("Test Patient");
        return patient;
    }

    private TimeSlot makeTimeSlot() {
        TimeSlot slot = new TimeSlot();
        slot.setDate(LocalDate.now());
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(9, 30));
        return slot;
    }

    private Appointment makeAppointment(Long id, Doctor doctor, Patient patient) {
        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);

        TimeSlot slot = makeTimeSlot();
        slot.setSchedule(schedule);

        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setTimeSlot(slot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointment;
    }

    private MedicalRecord makeMedicalRecord(Long id, Patient patient, Appointment appointment) {
        Medication med = new Medication();
        med.setId(1L);
        med.setName("Paracetamol 500mg");

        MedicalRecord record = new MedicalRecord();
        record.setId(id);
        record.setPatient(patient);
        record.setAppointment(appointment);
        record.setDiagnosis("Flu");
        record.setTreatmentPlan("Rest and hydration");
        record.setMedications(List.of(med));
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }
    // ── getVisitByAppointmentId ───────────────────────────────────────────────

    @Test
    void shouldReturnVisitResponse_whenRecordExists() {
        Patient patient = makePatient(1L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, patient);
        MedicalRecord record = makeMedicalRecord(1L, patient, appointment);

        when(medicalRecordRepository.findByAppointmentIdWithDetails(1L))
                .thenReturn(Optional.of(record));

        var response = medicalRecordService.getVisitByAppointmentId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAppointmentId());
        assertEquals(1L, response.getPatientId());
        assertEquals("Test Patient", response.getPatientFullName());
        assertEquals("Flu", response.getDiagnosis());
    }

    @Test
    void shouldThrow_whenVisitNotFound() {
        when(medicalRecordRepository.findByAppointmentIdWithDetails(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> medicalRecordService.getVisitByAppointmentId(1L));
    }
    // ── getPatientHistory ─────────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyList_whenPatientHasNoHistory() {
        when(medicalRecordRepository.findPatientHistory(1L))
                .thenReturn(Collections.emptyList());

        List<MedicalHistoryResponse> result = medicalRecordService.getPatientHistory(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnHistory_whenPatientHasRecords() {
        Patient patient = makePatient(1L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, patient);
        MedicalRecord record = makeMedicalRecord(1L, patient, appointment);

        when(medicalRecordRepository.findPatientHistory(1L))
                .thenReturn(List.of(record));

        List<MedicalHistoryResponse> result = medicalRecordService.getPatientHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Flu", result.get(0).getCondition());
        assertEquals("Rest and hydration", result.get(0).getTreatmentPlan());
        assertEquals(1, result.get(0).getMedications().size());
    }

    // ── getActiveMedications ──────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyList_whenPatientHasNoMedicalRecords() {
        when(medicalRecordRepository.findLatestRecordWithMedications(1L))
                .thenReturn(Collections.emptyList());

        List<ActiveMedicationResponse> result = medicalRecordService.getActiveMedications(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyList_whenLatestRecordHasNoMedications() {
        MedicalRecord record = new MedicalRecord();
        record.setMedications(Collections.emptyList());

        when(medicalRecordRepository.findLatestRecordWithMedications(1L))
                .thenReturn(List.of(record));

        List<ActiveMedicationResponse> result = medicalRecordService.getActiveMedications(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnActiveMedications_whenLatestRecordHasMedications() {
        Patient patient = makePatient(1L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, patient);
        MedicalRecord record = makeMedicalRecord(1L, patient, appointment);

        when(medicalRecordRepository.findLatestRecordWithMedications(1L))
                .thenReturn(List.of(record));

        List<ActiveMedicationResponse> result = medicalRecordService.getActiveMedications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paracetamol 500mg", result.get(0).getName());
        assertEquals("Flu", result.get(0).getCondition());
    }

    // ── createDiagnosis ───────────────────────────────────────────────────────

    @Test
    void shouldCreateDiagnosis_successfully() throws Exception {
        Long patientId = 1L;
        Long doctorId = 2L;

        Patient patient = makePatient(patientId);
        Doctor doctor = makeDoctor(doctorId);
        Appointment appointment = makeAppointment(1L, doctor, patient);

        PatientDiagnosisRequest request = new PatientDiagnosisRequest(
                1L, "Flu", "Rest", null, "Paracetamol 500mg", null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndPatientId(doctorId, patientId))
                .thenReturn(true);
        when(medicalRecordRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(medicalRecordRepository.save(any())).thenAnswer(i -> {
            MedicalRecord saved = i.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        DiagnosisResponse response = medicalRecordService.createDiagnosis(
                patientId, request, doctorId);

        assertNotNull(response);
        assertEquals("Flu", response.diagnosis());
        assertEquals("Rest", response.treatmentPlan());
    }

    @Test
    void shouldThrow_whenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        PatientDiagnosisRequest request = new PatientDiagnosisRequest(
                1L, "Flu", "Rest", null, "Paracetamol 500mg", null);

        assertThrows(RuntimeException.class,
                () -> medicalRecordService.createDiagnosis(1L, request, 2L));
    }

    @Test
    void shouldThrow_whenAppointmentDoesNotBelongToPatient() {
        Patient patient = makePatient(1L);
        Patient otherPatient = makePatient(99L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, otherPatient);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        PatientDiagnosisRequest request = new PatientDiagnosisRequest(
                1L, "Flu", "Rest", null, "Paracetamol 500mg", null);

        assertThrows(RuntimeException.class,
                () -> medicalRecordService.createDiagnosis(1L, request, 2L));
    }

    @Test
    void shouldThrow_whenDoctorNotAssignedToAppointment() {
        Patient patient = makePatient(1L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, patient);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        PatientDiagnosisRequest request = new PatientDiagnosisRequest(
                1L, "Flu", "Rest", null, "Paracetamol 500mg", null);

        assertThrows(Exception.class,
                () -> medicalRecordService.createDiagnosis(1L, request, 99L));
    }

    @Test
    void shouldThrow_whenDiagnosisAlreadyExists() {
        Patient patient = makePatient(1L);
        Doctor doctor = makeDoctor(2L);
        Appointment appointment = makeAppointment(1L, doctor, patient);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndPatientId(2L, 1L)).thenReturn(true);
        when(medicalRecordRepository.existsByAppointmentId(1L)).thenReturn(true);

        PatientDiagnosisRequest request = new PatientDiagnosisRequest(
                1L, "Flu", "Rest", null, "Paracetamol 500mg", null);

        assertThrows(RuntimeException.class,
                () -> medicalRecordService.createDiagnosis(1L, request, 2L));
    }
}