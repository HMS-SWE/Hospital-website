package com.example.backend.service;

import com.example.backend.dto.diagnosis.DiagnosisRequest;
import com.example.backend.dto.diagnosis.DiagnosisResponse;
import com.example.backend.dto.diagnosis.VisitDiagnosisResponse;
import com.example.backend.entity.*;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.MedicalRecordRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;
    @Mock
    MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    DiagnosisService diagnosisService;

    private Doctor assignedDoctor;
    private Patient patient;
    private Appointment appointment;
    private Long assignedDoctorId;
    private Long otherDoctorId;

    @BeforeEach
    void setUp() {
        assignedDoctor = Doctor.builder().build();
        setId(assignedDoctor, 10L);

        patient = Patient.builder().build();
        setId(patient, 20L);
        patient.setFullName("Jane Doe");

        TimeSlot slot = TimeSlot.builder()
                .date(LocalDate.of(2025, 6, 1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .build();
        setId(slot, 5L);

        appointment = Appointment.builder()
                .doctor(assignedDoctor)
                .patient(patient)
                .timeSlot(slot)
                .build();
        setId(appointment, 1L);

        assignedDoctorId = 10L;

        otherDoctorId = 99L;
    }

    // ── GET /api/visits/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("getVisitWithDiagnosis")
    class GetVisit {

        @Test
        @DisplayName("returns visit details with existing diagnosis and medications")
        void returnsVisitWithExistingDiagnosis() {
            MedicalRecord record = buildRecord("Flu", "Rest + fluids", List.of("Paracetamol 500mg"));
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(1L))
                    .thenReturn(Optional.of(record));

            VisitDiagnosisResponse resp = diagnosisService.getVisitWithDiagnosis(1L, assignedDoctorId);

            assertThat(resp.visitId()).isEqualTo(1L);
            assertThat(resp.patientFullName()).isEqualTo("Jane Doe");
            assertThat(resp.diagnosis()).isEqualTo("Flu");
            assertThat(resp.treatmentPlan()).isEqualTo("Rest + fluids");
            assertThat(resp.medications()).hasSize(1);
            assertThat(resp.medications().get(0).name()).isEqualTo("Paracetamol 500mg");
        }

        @Test
        @DisplayName("returns null diagnosis fields when no record exists yet")
        void returnsNullDiagnosisWhenNoRecord() {
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(1L))
                    .thenReturn(Optional.empty());

            VisitDiagnosisResponse resp = diagnosisService.getVisitWithDiagnosis(1L, assignedDoctorId);

            assertThat(resp.diagnosis()).isNull();
            assertThat(resp.medications()).isEmpty();
        }

        @Test
        @DisplayName("throws EntityNotFoundException when appointment does not exist")
        void throws404WhenAppointmentMissing() {
            when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> diagnosisService.getVisitWithDiagnosis(999L, assignedDoctorId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("throws AccessDeniedException when doctor is not assigned to the visit")
        void throws403WhenNotAssignedDoctor() {
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

            assertThatThrownBy(() -> diagnosisService.getVisitWithDiagnosis(1L, otherDoctorId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    // ── POST /api/visits/{id}/diagnosis ──────────────────────────────────

    @Nested
    @DisplayName("saveOrUpdateDiagnosis")
    class SaveOrUpdate {

        private DiagnosisRequest validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new DiagnosisRequest(
                    "Type 2 Diabetes",
                    "Low-sugar diet and daily walks",
                    "Metformin 500mg",
                    "Metformin 500mg\nAspirin 100mg");
        }

        @Test
        @DisplayName("creates a new medical record when none exists")
        void createsDiagnosisOnFirstCall() {
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(1L))
                    .thenReturn(Optional.empty());
            when(medicalRecordRepository.save(any())).thenAnswer(inv -> {
                MedicalRecord r = inv.getArgument(0);
                setId(r, 100L);
                return r;
            });

            DiagnosisResponse resp = diagnosisService.saveOrUpdateDiagnosis(1L, validRequest, assignedDoctorId);

            assertThat(resp.diagnosis()).isEqualTo("Type 2 Diabetes");
            assertThat(resp.treatmentPlan()).isEqualTo("Low-sugar diet and daily walks");
            assertThat(resp.medications()).hasSize(2);
            assertThat(resp.medications()).extracting("name")
                    .containsExactly("Metformin 500mg", "Aspirin 100mg");
            verify(medicalRecordRepository).save(any(MedicalRecord.class));
        }

        @Test
        @DisplayName("updates existing record — old medications are replaced")
        void updatesDiagnosisWhenRecordExists() {
            MedicalRecord existing = buildRecord("Old diagnosis", "Old plan", List.of("OldMed"));
            setId(existing, 100L);
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(1L))
                    .thenReturn(Optional.of(existing));
            when(medicalRecordRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            DiagnosisResponse resp = diagnosisService.saveOrUpdateDiagnosis(1L, validRequest, assignedDoctorId);

            assertThat(resp.diagnosis()).isEqualTo("Type 2 Diabetes");
            assertThat(resp.medications()).extracting("name")
                    .containsExactly("Metformin 500mg", "Aspirin 100mg");
        }

        @Test
        @DisplayName("same doctor + same patient on a different visit is not a duplicate")
        void samePatientDifferentVisitIsNotDuplicate() {
            Appointment secondAppointment = Appointment.builder()
                    .doctor(assignedDoctor)
                    .patient(patient)
                    .timeSlot(appointment.getTimeSlot())
                    .build();
            setId(secondAppointment, 2L);

            when(appointmentRepository.findById(2L)).thenReturn(Optional.of(secondAppointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(2L))
                    .thenReturn(Optional.empty());
            when(medicalRecordRepository.save(any())).thenAnswer(inv -> {
                MedicalRecord r = inv.getArgument(0);
                setId(r, 200L);
                return r;
            });

            DiagnosisResponse resp = diagnosisService.saveOrUpdateDiagnosis(2L, validRequest, assignedDoctorId);

            assertThat(resp.medicalRecordId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("throws AccessDeniedException when a different doctor attempts to write diagnosis")
        void throws403WhenUnauthorizedDoctor() {
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

            assertThatThrownBy(() -> diagnosisService.saveOrUpdateDiagnosis(1L, validRequest, otherDoctorId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(medicalRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("propagates exception on save failure — transaction rolls back")
        void transactionalRollbackOnSaveFailure() {
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
            when(medicalRecordRepository.findByAppointmentIdWithMedications(1L))
                    .thenReturn(Optional.empty());
            when(medicalRecordRepository.save(any()))
                    .thenThrow(new RuntimeException("DB constraint violation"));

            assertThatThrownBy(() -> diagnosisService.saveOrUpdateDiagnosis(1L, validRequest, assignedDoctorId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB constraint violation");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private MedicalRecord buildRecord(String diagnosis, String plan, List<String> medNames) {
        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .doctor(assignedDoctor)
                .patient(patient)
                .diagnosis(diagnosis)
                .treatmentPlan(plan)
                .startDate(LocalDate.now())
                .build();
        setId(record, 50L);

        List<Medication> meds = new ArrayList<>();
        for (String name : medNames) {
            Medication m = Medication.builder().medicalRecord(record).name(name).build();
            meds.add(m);
        }
        record.getMedications().addAll(meds);
        return record;
    }

    private void setId(BaseEntity entity, Long id) {
        try {
            var field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}