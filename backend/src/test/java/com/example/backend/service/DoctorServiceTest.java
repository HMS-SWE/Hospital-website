package com.example.backend.service;

import com.example.backend.dto.doctor.DoctorSearchResponse;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Specialty;
import com.example.backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        Specialty specialty = new Specialty();
        setField(specialty, "name", "Cardiology");

        doctor = Doctor.builder()
                .fullName("Dr. Ahmed Ali")
                .department("Cardiology Dept")
                .degree("MD")
                .examinationPrice(250.0f)
                .specialty(specialty)
                .build();
        setField(doctor, "id", 1L);
    }

    @Test
    @DisplayName("searchDoctors with null query → calls findAllActive")
    void searchDoctorsNullQuery() {
        when(doctorRepository.findAllActive()).thenReturn(List.of(doctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors(null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFullName()).isEqualTo("Dr. Ahmed Ali");
        verify(doctorRepository).findAllActive();
        verify(doctorRepository, never()).searchByNameOrSpecialty(any());
    }

    @Test
    @DisplayName("searchDoctors with blank query → calls findAllActive")
    void searchDoctorsBlankQuery() {
        when(doctorRepository.findAllActive()).thenReturn(List.of(doctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors("   ");

        assertThat(results).hasSize(1);
        verify(doctorRepository).findAllActive();
        verify(doctorRepository, never()).searchByNameOrSpecialty(any());
    }

    @Test
    @DisplayName("searchDoctors with empty string → calls findAllActive")
    void searchDoctorsEmptyQuery() {
        when(doctorRepository.findAllActive()).thenReturn(List.of(doctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors("");

        verify(doctorRepository).findAllActive();
    }

    @Test
    @DisplayName("searchDoctors with non-empty query → calls searchByNameOrSpecialty")
    void searchDoctorsWithQuery() {
        when(doctorRepository.searchByNameOrSpecialty("Ahmed")).thenReturn(List.of(doctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors("Ahmed");

        assertThat(results).hasSize(1);
        verify(doctorRepository).searchByNameOrSpecialty("Ahmed");
        verify(doctorRepository, never()).findAllActive();
    }

    @Test
    @DisplayName("searchDoctors trims whitespace from query before searching")
    void searchDoctorsTrimmedQuery() {
        when(doctorRepository.searchByNameOrSpecialty("Ahmed")).thenReturn(List.of(doctor));

        doctorService.searchDoctors("  Ahmed  ");

        verify(doctorRepository).searchByNameOrSpecialty("Ahmed");
    }

    @Test
    @DisplayName("searchDoctors returns empty list when no results found")
    void searchDoctorsNoResults() {
        when(doctorRepository.searchByNameOrSpecialty("Unknown")).thenReturn(List.of());

        List<DoctorSearchResponse> results = doctorService.searchDoctors("Unknown");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("toResponse maps all Doctor fields correctly")
    void toResponseMapsAllFields() {
        when(doctorRepository.findAllActive()).thenReturn(List.of(doctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors(null);

        DoctorSearchResponse response = results.get(0);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Dr. Ahmed Ali");
        assertThat(response.getSpecialtyName()).isEqualTo("Cardiology");
        assertThat(response.getExaminationPrice()).isEqualTo(250.0f);
        assertThat(response.getDepartment()).isEqualTo("Cardiology Dept");
        assertThat(response.getDegree()).isEqualTo("MD");
    }

    @Test
    @DisplayName("toResponse maps null optional fields without throwing")
    void toResponseNullOptionalFields() {
        Specialty specialty = new Specialty();
        setField(specialty, "name", "General");

        Doctor minimalDoctor = Doctor.builder()
                .fullName("Dr. Minimal")
                .specialty(specialty)
                .build();
        setField(minimalDoctor, "id", 2L);

        when(doctorRepository.findAllActive()).thenReturn(List.of(minimalDoctor));

        List<DoctorSearchResponse> results = doctorService.searchDoctors(null);

        assertThat(results.get(0).getDepartment()).isNull();
        assertThat(results.get(0).getDegree()).isNull();
        assertThat(results.get(0).getExaminationPrice()).isNull();
    }

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