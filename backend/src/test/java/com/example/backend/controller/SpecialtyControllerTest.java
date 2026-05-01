package com.example.backend.controller;

import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.dto.specialty.SpecialtyRequest;
import com.example.backend.dto.specialty.SpecialtyResponse;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.SpecialtyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecialtyController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT filter
class SpecialtyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SpecialtyService specialtyService;
    @MockitoBean private com.example.backend.service.JwtService jwtService;
    @MockitoBean private com.example.backend.repository.UserRepository userRepository;

    private SpecialtyResponse stubResponse;

    @BeforeEach
    void setUp() {
        stubResponse = SpecialtyResponse.builder()
                .id(1L)
                .name("Cardiology")
                .location("Building A")
                .doctorCount(3)
                .build();
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/specialties → 200 with list")
    void getAll_returns200() throws Exception {
        when(specialtyService.getAll()).thenReturn(List.of(stubResponse));

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cardiology"))
                .andExpect(jsonPath("$[0].doctorCount").value(3));
    }

    @Test
    @DisplayName("GET /api/specialties → returns empty list when none exist")
    void getAll_returnsEmptyList() throws Exception {
        when(specialtyService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET ONE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/specialties/{id} → 200 when found")
    void getById_returns200() throws Exception {
        when(specialtyService.getById(1L)).thenReturn(stubResponse);

        mockMvc.perform(get("/api/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.location").value("Building A"));
    }

    @Test
    @DisplayName("GET /api/specialties/{id} → 404 when not found")
    void getById_returns404() throws Exception {
        when(specialtyService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Specialty not found with id: 99"));

        mockMvc.perform(get("/api/specialties/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Specialty not found with id: 99"));
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/specialties → 201 when valid")
    void create_returns201() throws Exception {
        when(specialtyService.create(any())).thenReturn(stubResponse);

        mockMvc.perform(post("/api/specialties").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cardiology"));
    }

    @Test
    @DisplayName("POST /api/specialties → 409 when name duplicate")
    void create_returns409_whenDuplicate() throws Exception {
        when(specialtyService.create(any()))
                .thenThrow(new ConflictException("Specialty with name 'Cardiology' already exists"));

        mockMvc.perform(post("/api/specialties").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Specialty with name 'Cardiology' already exists"));
    }

    @Test
    @DisplayName("POST /api/specialties → 400 when name blank")
    void create_returns400_whenNameBlank() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(
                new SpecialtyRequest("", "Building A")
        );

        mockMvc.perform(post("/api/specialties").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    @DisplayName("POST /api/specialties → 400 when location blank")
    void create_returns400_whenLocationBlank() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(
                new SpecialtyRequest("Cardiology", "")
        );

        mockMvc.perform(post("/api/specialties").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("location"));
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/specialties/{id} → 200 when valid")
    void update_returns200() throws Exception {
        SpecialtyResponse updated = SpecialtyResponse.builder()
                .id(1L)
                .name("Cardiology Updated")
                .location("Building B")
                .doctorCount(3)
                .build();

        when(specialtyService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/specialties/1").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SpecialtyRequest("Cardiology Updated", "Building B")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cardiology Updated"))
                .andExpect(jsonPath("$.location").value("Building B"));
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} → 409 when name taken by another")
    void update_returns409_whenNameTaken() throws Exception {
        when(specialtyService.update(eq(1L), any()))
                .thenThrow(new ConflictException("Specialty with name 'Neurology' already exists"));

        mockMvc.perform(put("/api/specialties/1").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SpecialtyRequest("Neurology", "Building C")
                        )))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} → 404 when not found")
    void update_returns404_whenNotFound() throws Exception {
        when(specialtyService.update(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Specialty not found with id: 99"));

        mockMvc.perform(put("/api/specialties/99").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isNotFound());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/specialties/{id} → 204 when no doctors assigned")
    void delete_returns204() throws Exception {
        doNothing().when(specialtyService).delete(1L);

        mockMvc.perform(delete("/api/specialties/1").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN))
                .andExpect(status().isNoContent());

        verify(specialtyService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} → 409 when doctors assigned")
    void delete_returns409_whenDoctorsAssigned() throws Exception {
        doThrow(new ConflictException("Cannot delete specialty 'Cardiology' — 3 doctor(s) are still assigned to it"))
                .when(specialtyService).delete(1L);

        mockMvc.perform(delete("/api/specialties/1").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Cannot delete specialty 'Cardiology' — 3 doctor(s) are still assigned to it"));
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} → 404 when not found")
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty not found with id: 99"))
                .when(specialtyService).delete(99L);

        mockMvc.perform(delete("/api/specialties/99").requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.ADMIN))
                .andExpect(status().isNotFound());
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    private String validRequest() throws Exception {
        return objectMapper.writeValueAsString(
                new SpecialtyRequest("Cardiology", "Building A")
        );
    }
}