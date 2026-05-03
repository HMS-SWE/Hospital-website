package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RefreshTokenRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.enums.Role;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_shouldReturn200AndPatientResponse_whenRequestIsValid() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Patient");
        request.setMiddleName("One");
        request.setLastName("Test");
        request.setNationalId("12345678901234");
        request.setDob("2000-01-01");
        request.setGender("Male");
        request.setEmail("patient@hospital.com");
        request.setPhone("+201001234567");
        request.setEmergency("+201009876543");
        request.setPassword("Patient@123");
        request.setRole("patient");

        RegisterResponse responseBody = new RegisterResponse(
                10L,
                "patient@hospital.com",
                "patient-access-token",
                "patient-refresh-token",
                Role.PATIENT,
                3600L
        );

        when(authService.register(request)).thenReturn(responseBody);

        ResponseEntity<RegisterResponse> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getUserId());
        assertEquals("patient@hospital.com", response.getBody().getEmail());
        assertEquals("patient-access-token", response.getBody().getAccessToken());
        assertEquals("patient-refresh-token", response.getBody().getRefreshToken());
        assertEquals(Role.PATIENT, response.getBody().getRole());
        assertEquals(3600L, response.getBody().getExpiresIn());
    }

    @Test
    void login_shouldReturn200AndTokensAndRole_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@hospital.com");
        request.setPassword("Admin@1234");

        LoginResponse responseBody = new LoginResponse(
                "mock-access-token",
                "mock-refresh-token",
                Role.ADMIN,
                3600L
        );

        when(authService.login(request)).thenReturn(responseBody);

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock-access-token", response.getBody().getAccessToken());
        assertEquals("mock-refresh-token", response.getBody().getRefreshToken());
        assertEquals(Role.ADMIN, response.getBody().getRole());
        assertEquals(3600L, response.getBody().getExpiresIn());
    }

    @Test
    void refresh_shouldReturn200AndNewTokens_whenRefreshTokenIsValid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        LoginResponse responseBody = new LoginResponse(
                "new-access-token",
                "new-refresh-token",
                Role.DOCTOR,
                3600L
        );

        when(authService.refresh("valid-refresh-token")).thenReturn(responseBody);

        ResponseEntity<LoginResponse> response = authController.refresh(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new-access-token", response.getBody().getAccessToken());
        assertEquals("new-refresh-token", response.getBody().getRefreshToken());
        assertEquals(Role.DOCTOR, response.getBody().getRole());
        assertEquals(3600L, response.getBody().getExpiresIn());
    }
}
