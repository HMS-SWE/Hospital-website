package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
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
        request.setUserName("patient1");
        request.setFullName("Patient One");
        request.setEmail("patient@hospital.com");
        request.setPassword("Patient@123");
        request.setPhoneNumber("+201001234567");

        RegisterResponse responseBody =
                new RegisterResponse(10L, "patient@hospital.com", "patient-jwt-token", Role.PATIENT, 3600L);

        when(authService.register(request)).thenReturn(responseBody);

        ResponseEntity<RegisterResponse> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getUserId());
        assertEquals("patient@hospital.com", response.getBody().getEmail());
        assertEquals("patient-jwt-token", response.getBody().getToken());
        assertEquals(Role.PATIENT, response.getBody().getRole());
        assertEquals(3600L, response.getBody().getExpiresIn());
    }

    @Test
    void login_shouldReturn200AndTokenAndRole_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@hospital.com");
        request.setPassword("Admin@1234");

        LoginResponse responseBody = new LoginResponse("mock-jwt-token", Role.ADMIN, 3600L);

        when(authService.login(request)).thenReturn(responseBody);

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock-jwt-token", response.getBody().getToken());
        assertEquals(Role.ADMIN, response.getBody().getRole());
        assertEquals(3600L, response.getBody().getExpiresIn());
    }
}
