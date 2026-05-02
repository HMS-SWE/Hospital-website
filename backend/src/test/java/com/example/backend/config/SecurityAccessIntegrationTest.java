package com.example.backend.config;

import com.example.backend.controller.AuthController;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtAuthenticationFilter;
import com.example.backend.security.OAuth2FailureHandler;
import com.example.backend.security.OAuth2SuccessHandler;
import com.example.backend.service.AuthService;
import com.example.backend.service.CustomOAuth2UserService;
import com.example.backend.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityAccessIntegrationTest.TestEndpointsConfig.class
})
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    // ── New beans required by the merged SecurityConfig ───────────────────────
    @MockitoBean
    private CustomOAuth2UserService oAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockitoBean
    private OAuth2FailureHandler oAuth2FailureHandler;

    @TestConfiguration
    static class TestEndpointsConfig {
        @Bean
        TestSecurityEndpoints testSecurityEndpoints() {
            return new TestSecurityEndpoints();
        }
    }

    @RestController
    static class TestSecurityEndpoints {

        @GetMapping("/api/admin/health")
        public ResponseEntity<String> adminHealth() {
            return ResponseEntity.ok("admin ok");
        }

        @GetMapping("/api/doctors/health")
        public ResponseEntity<String> doctorHealth() {
            return ResponseEntity.ok("doctor ok");
        }

        @GetMapping("/api/patients/health")
        public ResponseEntity<String> patientHealth() {
            return ResponseEntity.ok("patient ok");
        }

        @GetMapping("/api/schedules/health")
        public ResponseEntity<String> scheduleHealth() {
            return ResponseEntity.ok("schedule ok");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Admin accessing /api/admin/** -> allowed")
    void adminAccessingAdminEndpoint_shouldBeAllowed() throws Exception {
        // /api/admin/** IS a sensitive route — DB will be called
        mockAuthenticatedUser("admin-token", 1L, Role.ADMIN, true);

        mockMvc.perform(get("/api/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin ok"));
    }

    @Test
    @DisplayName("Patient accessing /api/admin/** -> denied")
    void patientAccessingAdminEndpoint_shouldBeDenied() throws Exception {
        // Role check happens before DB — no DB stub needed
        mockAuthenticatedUser("patient-token", 2L, Role.PATIENT, false);

        mockMvc.perform(get("/api/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer patient-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @DisplayName("Doctor accessing /api/admin/** -> denied")
    void doctorAccessingAdminEndpoint_shouldBeDenied() throws Exception {
        // Role check happens before DB — no DB stub needed
        mockAuthenticatedUser("doctor-token", 3L, Role.DOCTOR, false);

        mockMvc.perform(get("/api/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer doctor-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @DisplayName("Doctor accessing /api/doctors/** -> allowed")
    void doctorAccessingDoctorEndpoint_shouldBeAllowed() throws Exception {
        // /api/doctors/** is NOT a sensitive route — no DB call
        mockAuthenticatedUser("doctor-token", 3L, Role.DOCTOR, false);

        mockMvc.perform(get("/api/doctors/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer doctor-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("doctor ok"));
    }

    @Test
    @DisplayName("Unauthenticated user accessing protected endpoint -> denied")
    void unauthenticatedUserAccessingProtectedEndpoint_shouldBeDenied() throws Exception {
        mockMvc.perform(get("/api/admin/health"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing or malformed Authorization header"));
    }

    @Test
    @DisplayName("Public endpoints accessible without login")
    void publicAuthEndpoint_shouldBeAccessibleWithoutLogin() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse("jwt-token", Role.ADMIN, 3600L));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@hospital.com",
                                  "password": "Admin@1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Valid token -> success")
    void validToken_shouldSucceed() throws Exception {
        // /api/schedules/** is NOT a sensitive route — no DB call
        mockAuthenticatedUser("valid-token", 4L, Role.DOCTOR, false);

        mockMvc.perform(get("/api/schedules/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("schedule ok"));
    }

    @Test
    @DisplayName("Invalid token -> rejected")
    void invalidToken_shouldBeRejected() throws Exception {
        when(jwtService.validateToken("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }

    @Test
    @DisplayName("Expired token -> rejected")
    void expiredToken_shouldBeRejected() throws Exception {
        when(jwtService.validateToken("expired-token")).thenReturn(false);

        mockMvc.perform(get("/api/doctors/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }

    @Test
    @DisplayName("Patient accessing /api/patients/** -> allowed")
    void patientAccessingPatientEndpoint_shouldBeAllowed() throws Exception {
        // /api/patients/** is NOT a sensitive route — no DB call
        mockAuthenticatedUser("patient-token", 2L, Role.PATIENT, false);

        mockMvc.perform(get("/api/patients/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer patient-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("patient ok"));
    }

    @Test
    @DisplayName("Malformed Authorization header -> rejected")
    void malformedAuthHeader_shouldBeRejected() throws Exception {
        mockMvc.perform(get("/api/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "InvalidHeader"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing or malformed Authorization header"));
    }

    @Test
    @DisplayName("Admin accessing /api/doctors/** -> allowed")
    void adminAccessingDoctorEndpoint_shouldBeAllowed() throws Exception {
        // /api/doctors/** is NOT a sensitive route — no DB call
        mockAuthenticatedUser("admin-token", 1L, Role.ADMIN, false);

        mockMvc.perform(get("/api/doctors/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Stubs JWT service claims for every test.
     * Only stubs userRepository when isSensitiveRoute=true, matching the filter's
     * behaviour of hitting the DB only on sensitive routes.
     */
    private void mockAuthenticatedUser(String token, Long userId, Role role,
                                       boolean isSensitiveRoute) {
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractRole(token)).thenReturn(role);

        if (isSensitiveRoute) {
            User user = User.builder()
                    .id(userId)
                    .userName(role.name().toLowerCase())
                    .fullName(role.name() + " User")
                    .email(role.name().toLowerCase() + "@hospital.com")
                    .password("encoded-password")
                    .role(role)
                    .build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        }
    }
}