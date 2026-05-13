package com.example.backend.mapper;

import com.example.backend.dto.profile.response.PatientProfileResponse;
import com.example.backend.entity.Patient;
import com.example.backend.enums.ChronicDisease;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private PatientMapper patientMapper;

    @BeforeEach
    void setUp() {
        patientMapper = new PatientMapper();
    }

    @Test
    @DisplayName("maps all Patient fields to PatientProfileResponse correctly")
    void mapsAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.of(2025, 2, 20, 14, 30);
        LocalDate birthDate = LocalDate.of(1995, 11, 3);

        Patient patient = Patient.builder()
                .userName("janedoe")
                .fullName("Jane Doe")
                .email("jane@example.com")
                .role(Role.PATIENT)
                .gender(Gender.FEMALE)
                .birthDate(birthDate)
                .phoneNumber("01123456789")
                .address("789 Patient Ave")
                .profilePicturePath("/images/jane.jpg")
                .emergencyNumber("01098765432")
                .whatsappNumber("01112345678")
                .bloodType("A+")
                .chronicDisease(ChronicDisease.DIABETES)
                .build();

        setField(patient, "id", 20L);
        setField(patient, "createdAt", now);

        PatientProfileResponse response = patientMapper.toResponse(patient);

        // inherited User fields
        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getUserName()).isEqualTo("janedoe");
        assertThat(response.getFullName()).isEqualTo("Jane Doe");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getRole()).isEqualTo(Role.PATIENT);
        assertThat(response.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(response.getBirthDate()).isEqualTo(birthDate);
        assertThat(response.getPhoneNumber()).isEqualTo("01123456789");
        assertThat(response.getAddress()).isEqualTo("789 Patient Ave");
        assertThat(response.getProfilePicturePath()).isEqualTo("/images/jane.jpg");
        assertThat(response.getCreatedAt()).isEqualTo(now);

        // Patient-specific fields
        assertThat(response.getEmergencyNumber()).isEqualTo("01098765432");
        assertThat(response.getWhatsappNumber()).isEqualTo("01112345678");
        assertThat(response.getBloodType()).isEqualTo("A+");
        assertThat(response.getChronicDisease()).isEqualTo(ChronicDisease.DIABETES);
    }

    @Test
    @DisplayName("maps Patient with null optional fields without throwing")
    void mapsNullOptionalPatientFields() {
        Patient patient = Patient.builder()
                .userName("minimalpatient")
                .fullName("Minimal Patient")
                .email("minimal@example.com")
                .build();

        PatientProfileResponse response = patientMapper.toResponse(patient);

        assertThat(response.getUserName()).isEqualTo("minimalpatient");
        assertThat(response.getFullName()).isEqualTo("Minimal Patient");
        assertThat(response.getEmail()).isEqualTo("minimal@example.com");
        assertThat(response.getGender()).isNull();
        assertThat(response.getBirthDate()).isNull();
        assertThat(response.getEmergencyNumber()).isNull();
        assertThat(response.getWhatsappNumber()).isNull();
        assertThat(response.getBloodType()).isNull();
        assertThat(response.getChronicDisease()).isNull();
    }

    @Test
    @DisplayName("maps each ChronicDisease enum value correctly")
    void mapsAllChronicDiseaseValues() {
        for (ChronicDisease disease : ChronicDisease.values()) {
            Patient patient = Patient.builder()
                    .userName("patient")
                    .fullName("Test Patient")
                    .email("test@example.com")
                    .chronicDisease(disease)
                    .build();

            PatientProfileResponse response = patientMapper.toResponse(patient);

            assertThat(response.getChronicDisease())
                    .as("ChronicDisease value %s should map correctly", disease)
                    .isEqualTo(disease);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private void setField(Object target, String fieldName, Object value) {
        try {
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new RuntimeException("Field not found: " + fieldName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}