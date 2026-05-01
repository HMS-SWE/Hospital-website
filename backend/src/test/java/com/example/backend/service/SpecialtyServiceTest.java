package com.example.backend.service;

import com.example.backend.dto.specialty.SpecialtyRequest;
import com.example.backend.dto.specialty.SpecialtyResponse;
import com.example.backend.entity.Specialty;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SpecialtyRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock private SpecialtyRepository specialtyRepository;
    @InjectMocks private SpecialtyService specialtyService;

    private Specialty mockSpecialty;

    @BeforeEach
    void setUp() {
        mockSpecialty = Specialty.builder()
                .name("Cardiology")
                .location("Building A")
                .build();
        mockSpecialty.setId(1L);
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create → saves and returns response")
    void create_savesAndReturns() {
        SpecialtyRequest request = new SpecialtyRequest("Cardiology", "Building A");

        when(specialtyRepository.existsByName("Cardiology")).thenReturn(false);
        when(specialtyRepository.save(any())).thenReturn(mockSpecialty);
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(3);

        SpecialtyResponse response = specialtyService.create(request);

        assertThat(response.getName()).isEqualTo("Cardiology");
        assertThat(response.getDoctorCount()).isEqualTo(3);
        verify(specialtyRepository).save(any());
    }

    @Test
    @DisplayName("create → throws 409 when name already exists")
    void create_throwsConflict_whenNameExists() {
        when(specialtyRepository.existsByName("Cardiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.create(
                new SpecialtyRequest("Cardiology", "Building A")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");

        verify(specialtyRepository, never()).save(any());
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll → returns all specialties")
    void getAll_returnsAll() {
        when(specialtyRepository.findAll()).thenReturn(List.of(mockSpecialty));
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(2);

        List<SpecialtyResponse> result = specialtyService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
        assertThat(result.get(0).getDoctorCount()).isEqualTo(2);
    }

    // ── GET ONE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById → returns specialty when found")
    void getById_returnsSpecialty() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(0);

        SpecialtyResponse response = specialtyService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("getById → throws 404 when not found")
    void getById_throwsNotFound() {
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update → updates and returns response")
    void update_updatesAndReturns() {
        SpecialtyRequest request = new SpecialtyRequest("Neurology", "Building B");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.existsByNameAndIdNot("Neurology", 1L)).thenReturn(false);
        when(specialtyRepository.save(any())).thenReturn(mockSpecialty);
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(0);

        specialtyService.update(1L, request);

        assertThat(mockSpecialty.getName()).isEqualTo("Neurology");
        assertThat(mockSpecialty.getLocation()).isEqualTo("Building B");
        verify(specialtyRepository).save(mockSpecialty);
    }

    @Test
    @DisplayName("update → throws 409 when name taken by another specialty")
    void update_throwsConflict_whenNameTaken() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.existsByNameAndIdNot("Neurology", 1L)).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.update(1L,
                new SpecialtyRequest("Neurology", "Building B")))
                .isInstanceOf(ConflictException.class);

        verify(specialtyRepository, never()).save(any());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete → deletes when no doctors assigned")
    void delete_deletesSuccessfully() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(0);

        specialtyService.delete(1L);

        verify(specialtyRepository).delete(mockSpecialty);
    }

    @Test
    @DisplayName("delete → throws 409 when doctors still assigned")
    void delete_throwsConflict_whenDoctorsAssigned() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.countDoctorsBySpecialtyId(1L)).thenReturn(3);

        assertThatThrownBy(() -> specialtyService.delete(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("3 doctor(s)");

        verify(specialtyRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete → throws 404 when specialty not found")
    void delete_throwsNotFound_whenSpecialtyNotFound() {
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(specialtyRepository, never()).delete(any());
    }
}