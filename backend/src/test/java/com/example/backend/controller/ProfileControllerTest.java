package com.example.backend.controller;

import com.example.backend.dto.doctor.response.DoctorPublicResponse;
import com.example.backend.dto.profile.request.*;
import com.example.backend.dto.profile.response.*;
import com.example.backend.enums.ChronicDisease;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)          // loads only the controller layer
@AutoConfigureMockMvc(addFilters = false)     // skip JWT filter — we test that separately
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    private ObjectMapper objectMapper;

    // ── Shared response stubs ─────────────────────────────────────────────────

    private UserProfileResponse stubUserResponse;
    private DoctorProfileResponse stubDoctorFullResponse;
    private DoctorPublicResponse stubDoctorPublicResponse;
    private PatientProfileResponse stubPatientResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        stubUserResponse = UserProfileResponse.builder()
                .id(1L)
                .userName("admin")
                .fullName("System Admin")
                .email("admin@hospital.com")
                .role(Role.ADMIN)
                .build();

        stubDoctorFullResponse = DoctorProfileResponse.builder()
                .id(2L)
                .userName("drsmith")
                .fullName("Dr. Smith")
                .email("smith@hospital.com")
                .role(Role.DOCTOR)
                .specialtyName("Cardiology")
                .specialtyLocation("Building A")
                .licenseNumber("LIC-001")
                .examinationPrice(200.0f)
                .build();

        stubDoctorPublicResponse = DoctorPublicResponse.builder()
                .id(2L)
                .fullName("Dr. Smith")
                .specialtyName("Cardiology")
                .specialtyLocation("Building A")
                .licenseNumber("LIC-001")
                .examinationPrice(200.0f)
                .build();

        stubPatientResponse = PatientProfileResponse.builder()
                .id(3L)
                .userName("patient1")
                .fullName("Jane Doe")
                .email("jane@hospital.com")
                .role(Role.PATIENT)
                .bloodType("A+")
                .chronicDisease(ChronicDisease.DIABETES)
                .build();
    }

    // =========================================================================
    // GET /doctors/{id}/profile
    // =========================================================================

    @Test
    @DisplayName("GET doctor profile → admin gets full response")
    void getDoctorProfile_adminGetsFull() throws Exception {
        when(profileService.getDoctorProfile(2L)).thenReturn(stubDoctorFullResponse);

        mockMvc.perform(getWithAttrs("/api/doctors/2/profile", 1L, Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("smith@hospital.com"))
                .andExpect(jsonPath("$.licenseNumber").value("LIC-001"));

        verify(profileService).getDoctorProfile(2L);
        verify(profileService, never()).getDoctorPublicProfile(any());
    }

    @Test
    @DisplayName("GET doctor profile → doctor gets own full response")
    void getDoctorProfile_doctorGetsSelf() throws Exception {
        when(profileService.getDoctorProfile(2L)).thenReturn(stubDoctorFullResponse);

        // requesting user IS the doctor (same id = 2)
        mockMvc.perform(getWithAttrs("/api/doctors/2/profile", 2L, Role.DOCTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("smith@hospital.com"));

        verify(profileService).getDoctorProfile(2L);
    }

    @Test
    @DisplayName("GET doctor profile → doctor gets another doctor's public response")
    void getDoctorProfile_doctorGetsOtherDoctorPublic() throws Exception {
        when(profileService.getDoctorPublicProfile(2L)).thenReturn(stubDoctorPublicResponse);

        // requesting user is doctor with id=5 viewing doctor with id=2
        mockMvc.perform(getWithAttrs("/api/doctors/2/profile", 5L, Role.DOCTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. Smith"))
                // email should NOT be in public response
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(profileService).getDoctorPublicProfile(2L);
        verify(profileService, never()).getDoctorProfile(any());
    }

    @Test
    @DisplayName("GET doctor profile → patient gets public response")
    void getDoctorProfile_patientGetsPublic() throws Exception {
        when(profileService.getDoctorPublicProfile(2L)).thenReturn(stubDoctorPublicResponse);

        mockMvc.perform(getWithAttrs("/api/doctors/2/profile", 3L, Role.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licenseNumber").value("LIC-001"))
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(profileService).getDoctorPublicProfile(2L);
    }

    // =========================================================================
    // GET /patients/{id}/profile
    // =========================================================================

    @Test
    @DisplayName("GET patient profile → admin gets full response")
    void getPatientProfile_adminGetsFull() throws Exception {
        when(profileService.getPatientProfile(3L)).thenReturn(stubPatientResponse);

        mockMvc.perform(getWithAttrs("/api/patients/3/profile", 1L, Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloodType").value("A+"));
    }

    @Test
    @DisplayName("GET patient profile → doctor gets full response")
    void getPatientProfile_doctorGetsFull() throws Exception {
        when(profileService.getPatientProfile(3L)).thenReturn(stubPatientResponse);

        mockMvc.perform(getWithAttrs("/api/patients/3/profile", 2L, Role.DOCTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    @DisplayName("GET patient profile → patient gets own profile")
    void getPatientProfile_patientGetsSelf() throws Exception {
        when(profileService.getPatientProfile(3L)).thenReturn(stubPatientResponse);

        mockMvc.perform(getWithAttrs("/api/patients/3/profile", 3L, Role.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloodType").value("A+"));
    }

    @Test
    @DisplayName("GET patient profile → patient gets 403 viewing another patient")
    void getPatientProfile_patientViewsOther_returns403() throws Exception {
        // patient with id=3 tries to view patient with id=99
        mockMvc.perform(getWithAttrs("/api/patients/99/profile", 3L, Role.PATIENT))
                .andExpect(status().isForbidden());

        verify(profileService, never()).getPatientProfile(any());
    }

    // =========================================================================
    // GET /users/{id}/profile
    // =========================================================================

    @Test
    @DisplayName("GET user profile → admin gets any user")
    void getUserProfile_adminGetsAny() throws Exception {
        when(profileService.getUserProfile(1L)).thenReturn(stubUserResponse);

        mockMvc.perform(getWithAttrs("/api/users/1/profile", 1L, Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("admin"));
    }

    @Test
    @DisplayName("GET user profile → user gets own profile")
    void getUserProfile_userGetsSelf() throws Exception {
        when(profileService.getUserProfile(1L)).thenReturn(stubUserResponse);

        mockMvc.perform(getWithAttrs("/api/users/1/profile", 1L, Role.ADMIN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET user profile → user gets 403 viewing another user")
    void getUserProfile_userViewsOther_returns403() throws Exception {
        mockMvc.perform(getWithAttrs("/api/users/99/profile", 3L, Role.PATIENT))
                .andExpect(status().isForbidden());

        verify(profileService, never()).getUserProfile(any());
    }

    // =========================================================================
    // PUT /doctors/{id}/profile
    // =========================================================================

    @Test
    @DisplayName("PUT doctor profile → admin updates any doctor")
    void updateDoctorProfile_adminUpdatesAny() throws Exception {
        when(profileService.updateDoctorProfile(eq(2L), any())).thenReturn(stubDoctorFullResponse);

        mockMvc.perform(putWithAttrs("/api/doctors/2/profile", 1L, Role.ADMIN)
                        .content(validDoctorRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. Smith"));
    }

    @Test
    @DisplayName("PUT doctor profile → doctor updates own profile")
    void updateDoctorProfile_doctorUpdatesSelf() throws Exception {
        when(profileService.updateDoctorProfile(eq(2L), any())).thenReturn(stubDoctorFullResponse);

        mockMvc.perform(putWithAttrs("/api/doctors/2/profile", 2L, Role.DOCTOR)
                        .content(validDoctorRequest()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT doctor profile → doctor gets 403 updating another doctor")
    void updateDoctorProfile_doctorUpdatesOther_returns403() throws Exception {
        mockMvc.perform(putWithAttrs("/api/doctors/2/profile", 5L, Role.DOCTOR)
                        .content(validDoctorRequest()))
                .andExpect(status().isForbidden());

        verify(profileService, never()).updateDoctorProfile(any(), any());
    }

    @Test
    @DisplayName("PUT doctor profile → patient gets 403")
    void updateDoctorProfile_patientUpdates_returns403() throws Exception {
        mockMvc.perform(putWithAttrs("/api/doctors/2/profile", 3L, Role.PATIENT)
                        .content(validDoctorRequest()))
                .andExpect(status().isForbidden());

        verify(profileService, never()).updateDoctorProfile(any(), any());
    }

    @Test
    @DisplayName("PUT doctor profile → invalid body returns 400")
    void updateDoctorProfile_invalidBody_returns400() throws Exception {
        // missing required fields
        String invalidBody = objectMapper.writeValueAsString(
                DoctorProfileRequest.builder().build()
        );

        mockMvc.perform(putWithAttrs("/api/doctors/2/profile", 2L, Role.DOCTOR)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // PUT /patients/{id}/profile
    // =========================================================================

    @Test
    @DisplayName("PUT patient profile → admin updates any patient")
    void updatePatientProfile_adminUpdatesAny() throws Exception {
        when(profileService.updatePatientProfile(eq(3L), any())).thenReturn(stubPatientResponse);

        mockMvc.perform(putWithAttrs("/api/patients/3/profile", 1L, Role.ADMIN)
                        .content(validPatientRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloodType").value("A+"));
    }

    @Test
    @DisplayName("PUT patient profile → patient updates own profile")
    void updatePatientProfile_patientUpdatesSelf() throws Exception {
        when(profileService.updatePatientProfile(eq(3L), any())).thenReturn(stubPatientResponse);

        mockMvc.perform(putWithAttrs("/api/patients/3/profile", 3L, Role.PATIENT)
                        .content(validPatientRequest()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT patient profile → patient gets 403 updating another patient")
    void updatePatientProfile_patientUpdatesOther_returns403() throws Exception {
        mockMvc.perform(putWithAttrs("/api/patients/3/profile", 99L, Role.PATIENT)
                        .content(validPatientRequest()))
                .andExpect(status().isForbidden());

        verify(profileService, never()).updatePatientProfile(any(), any());
    }

    @Test
    @DisplayName("PUT patient profile → doctor gets 403 updating a patient")
    void updatePatientProfile_doctorUpdates_returns403() throws Exception {
        mockMvc.perform(putWithAttrs("/api/patients/3/profile", 2L, Role.DOCTOR)
                        .content(validPatientRequest()))
                .andExpect(status().isForbidden());

        verify(profileService, never()).updatePatientProfile(any(), any());
    }

    // =========================================================================
    // DELETE /users/{id}
    // =========================================================================

    @Test
    @DisplayName("DELETE user → admin deletes another user → 204")
    void deactivateUser_adminDeletesOther_returns204() throws Exception {
        doNothing().when(profileService).deactivateUser(3L, 1L);

        mockMvc.perform(deleteWithAttrs("/api/users/3", 1L, Role.ADMIN))
                .andExpect(status().isNoContent());

        verify(profileService).deactivateUser(3L, 1L);
    }

    @Test
    @DisplayName("DELETE user → admin deletes themselves → 403")
    void deactivateUser_adminDeletesSelf_returns403() throws Exception {
        mockMvc.perform(deleteWithAttrs("/api/users/1", 1L, Role.ADMIN))
                .andExpect(status().isForbidden());

        verify(profileService, never()).deactivateUser(any(), any());
    }

    @Test
    @DisplayName("DELETE user → non-admin tries to delete → 403")
    void deactivateUser_nonAdmin_returns403() throws Exception {
        mockMvc.perform(deleteWithAttrs("/api/users/3", 3L, Role.PATIENT))
                .andExpect(status().isForbidden());

        verify(profileService, never()).deactivateUser(any(), any());
    }

    // =========================================================================
    // HELPER BUILDERS — keep endpoints clean
    // =========================================================================

    // injects userId and role as request attributes (simulates what the filter does)
    private MockHttpServletRequestBuilder getWithAttrs(String url, Long userId, Role role) {
        return get(url)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, userId)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, role)
                .contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder putWithAttrs(String url, Long userId, Role role) {
        return put(url)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, userId)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, role)
                .contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder deleteWithAttrs(String url, Long userId, Role role) {
        return delete(url)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, userId)
                .requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, role);
    }

    private String validDoctorRequest() throws Exception {
        return objectMapper.writeValueAsString(
                DoctorProfileRequest.builder()
                        .userName("drsmith")
                        .fullName("Dr. Smith")
                        .email("smith@hospital.com")
                        .specialtyId(1L)
                        .department("Cardiology Dept")
                        .degree("MD")
                        .licenseNumber("LIC-001")
                        .examinationPrice(200.0f)
                        .build()
        );
    }

    private String validPatientRequest() throws Exception {
        return objectMapper.writeValueAsString(
                PatientProfileRequest.builder()
                        .userName("patient1")
                        .fullName("Jane Doe")
                        .email("jane@hospital.com")
                        .bloodType("A+")
                        .chronicDisease(ChronicDisease.DIABETES)
                        .build()
        );
    }
}