package com.example.backend.controller;

import com.example.backend.dto.diagnosis.*;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.DiagnosisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiagnosisController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiagnosisControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    DiagnosisService diagnosisService;
    @MockitoBean
    com.example.backend.service.JwtService jwtService;
    @MockitoBean
    com.example.backend.repository.UserRepository userRepository;

    private static final Long DOCTOR_ID = 10L;

    // ── GET /api/visits/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/visits/{id}")
    class GetVisit {

        @Test
        @DisplayName("200 — returns visit with existing diagnosis and medications")
        void returns200WithDiagnosisData() throws Exception {
            VisitDiagnosisResponse response = new VisitDiagnosisResponse(
                    1L, 20L, "Jane Doe",
                    LocalDate.of(2025, 6, 1), LocalTime.of(9, 0), LocalTime.of(9, 30),
                    100L, "Flu", "Rest and fluids", "Paracetamol 500mg",
                    List.of(new MedicationResponse(1L, "Paracetamol 500mg")));
            when(diagnosisService.getVisitWithDiagnosis(eq(1L), eq(DOCTOR_ID))).thenReturn(response);

            mockMvc.perform(get("/api/visits/1")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.patientFullName").value("Jane Doe"))
                    .andExpect(jsonPath("$.diagnosis").value("Flu"))
                    .andExpect(jsonPath("$.medications[0].name").value("Paracetamol 500mg"));
        }

        @Test
        @DisplayName("401 — unauthenticated request (no USER_ID attribute) is rejected")
        void returns401ForUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/visits/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("403 — doctor not assigned to visit is rejected")
        void returns403WhenDoctorNotAssigned() throws Exception {
            when(diagnosisService.getVisitWithDiagnosis(eq(1L), eq(DOCTOR_ID)))
                    .thenThrow(new AccessDeniedException("Not your appointment"));

            mockMvc.perform(get("/api/visits/1")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID))
                    .andExpect(status().isForbidden());
        }
    }

    // ── POST /api/visits/{id}/diagnosis ──────────────────────────────────

    @Nested
    @DisplayName("POST /api/visits/{id}/diagnosis")
    class PostDiagnosis {

        private DiagnosisRequest validRequest() {
            return new DiagnosisRequest(
                    "Type 2 Diabetes",
                    "Low-sugar diet and daily walks",
                    "Metformin 500mg",
                    "Metformin 500mg\nAspirin 100mg");
        }

        private DiagnosisResponse stubResponse() {
            return new DiagnosisResponse(
                    100L, 1L,
                    "Type 2 Diabetes",
                    "Low-sugar diet and daily walks",
                    "Metformin 500mg",
                    List.of(
                            new MedicationResponse(1L, "Metformin 500mg"),
                            new MedicationResponse(2L, "Aspirin 100mg")));
        }

        @Test
        @DisplayName("201 — first-time diagnosis creation")
        void returns201OnCreate() throws Exception {
            when(diagnosisService.diagnosisExists(1L)).thenReturn(false);
            when(diagnosisService.saveOrUpdateDiagnosis(eq(1L), any(), eq(DOCTOR_ID))).thenReturn(stubResponse());

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.medicalRecordId").value(100))
                    .andExpect(jsonPath("$.medications.length()").value(2))
                    .andExpect(jsonPath("$.medications[0].name").value("Metformin 500mg"));
        }

        @Test
        @DisplayName("200 — diagnosis update when record already exists")
        void returns200OnUpdate() throws Exception {
            when(diagnosisService.diagnosisExists(1L)).thenReturn(true);
            when(diagnosisService.saveOrUpdateDiagnosis(eq(1L), any(), eq(DOCTOR_ID))).thenReturn(stubResponse());

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 — blank diagnosis field fails validation")
        void returns400WhenDiagnosisBlank() throws Exception {
            DiagnosisRequest bad = new DiagnosisRequest("", "Some plan", null, "Aspirin 100mg");

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 — blank treatment plan fails validation")
        void returns400WhenTreatmentPlanBlank() throws Exception {
            DiagnosisRequest bad = new DiagnosisRequest("Flu", "", null, "Aspirin 100mg");

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 — blank medications field fails validation")
        void returns400WhenMedicationsBlank() throws Exception {
            DiagnosisRequest bad = new DiagnosisRequest("Flu", "Rest", null, "");

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 — blank line between medications fails validation")
        void returns400WhenMedicationsHasBlankLine() throws Exception {
            // "Aspirin\n\nIbuprofen" — empty line in the middle
            DiagnosisRequest bad = new DiagnosisRequest("Flu", "Rest", null, "Aspirin\n\nIbuprofen");

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("403 — doctor not assigned to visit is rejected")
        void returns403WhenDoctorNotAssigned() throws Exception {
            when(diagnosisService.diagnosisExists(1L)).thenReturn(false);
            when(diagnosisService.saveOrUpdateDiagnosis(eq(1L), any(), eq(DOCTOR_ID)))
                    .thenThrow(new AccessDeniedException("Not your appointment"));

            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, DOCTOR_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("401 — request without USER_ID attribute is rejected")
        void returns401ForUnauthenticated() throws Exception {
            mockMvc.perform(post("/api/visits/1/diagnosis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isUnauthorized());
        }
    }
}