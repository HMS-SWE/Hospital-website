package com.example.backend.mapper;

import com.example.backend.dto.profile.response.DoctorProfileResponse;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Specialty;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorMapperTest {

    private DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() {
        doctorMapper = new DoctorMapper();
    }

    @Test
    @DisplayName("maps all Doctor fields including specialty to DoctorProfileResponse correctly")
    void mapsAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.of(2025, 3, 10, 9, 0);
        LocalDate birthDate = LocalDate.of(1985, 7, 14);

        Specialty specialty = new Specialty();
        setField(specialty, "name", "Cardiology");
        setField(specialty, "location", "Building A, Floor 3");

        Doctor doctor = Doctor.builder()
                .userName("drsarah")
                .fullName("Dr. Sarah Ahmed")
                .email("sarah@hospital.com")
                .role(Role.DOCTOR)
                .gender(Gender.FEMALE)
                .birthDate(birthDate)
                .phoneNumber("01098765432")
                .address("456 Medical St")
                .profilePicturePath("/images/sarah.jpg")
                .specialty(specialty)
                .department("Cardiology Dept")
                .degree("MD, PhD")
                .qualifications("Board Certified Cardiologist")
                .licenseNumber("LIC-2025-001")
                .examinationPrice(300.0f)
                .consultationPrice(150.0f)
                .build();

        setField(doctor, "id", 10L);
        setField(doctor, "createdAt", now);

        DoctorProfileResponse response = doctorMapper.toResponse(doctor);

        // inherited User fields
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getUserName()).isEqualTo("drsarah");
        assertThat(response.getFullName()).isEqualTo("Dr. Sarah Ahmed");
        assertThat(response.getEmail()).isEqualTo("sarah@hospital.com");
        assertThat(response.getRole()).isEqualTo(Role.DOCTOR);
        assertThat(response.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(response.getBirthDate()).isEqualTo(birthDate);
        assertThat(response.getPhoneNumber()).isEqualTo("01098765432");
        assertThat(response.getAddress()).isEqualTo("456 Medical St");
        assertThat(response.getProfilePicturePath()).isEqualTo("/images/sarah.jpg");
        assertThat(response.getCreatedAt()).isEqualTo(now);

        // Doctor-specific fields
        assertThat(response.getSpecialtyName()).isEqualTo("Cardiology");
        assertThat(response.getSpecialtyLocation()).isEqualTo("Building A, Floor 3");
        assertThat(response.getDepartment()).isEqualTo("Cardiology Dept");
        assertThat(response.getDegree()).isEqualTo("MD, PhD");
        assertThat(response.getQualifications()).isEqualTo("Board Certified Cardiologist");
        assertThat(response.getLicenseNumber()).isEqualTo("LIC-2025-001");
        assertThat(response.getExaminationPrice()).isEqualTo(300.0f);
    }

    @Test
    @DisplayName("maps Doctor with null optional fields without throwing")
    void mapsNullOptionalDoctorFields() {
        Specialty specialty = new Specialty();
        setField(specialty, "name", "General");
        setField(specialty, "location", "Main Building");

        Doctor doctor = Doctor.builder()
                .userName("drminimal")
                .fullName("Dr. Minimal")
                .email("minimal@hospital.com")
                .specialty(specialty)
                .build();

        DoctorProfileResponse response = doctorMapper.toResponse(doctor);

        assertThat(response.getUserName()).isEqualTo("drminimal");
        assertThat(response.getSpecialtyName()).isEqualTo("General");
        assertThat(response.getSpecialtyLocation()).isEqualTo("Main Building");
        assertThat(response.getDepartment()).isNull();
        assertThat(response.getDegree()).isNull();
        assertThat(response.getQualifications()).isNull();
        assertThat(response.getLicenseNumber()).isNull();
        assertThat(response.getExaminationPrice()).isNull();
        assertThat(response.getConsultationPrice()).isNull();
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