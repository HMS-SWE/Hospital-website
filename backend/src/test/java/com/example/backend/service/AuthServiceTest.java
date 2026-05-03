package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreatePatientAndReturnToken_whenRequestIsValid() {
        RegisterRequest request = new RegisterRequest();
        request.setUserName("patient1");
        request.setFullName("Patient One");
        request.setEmail("patient@hospital.com");
        request.setPassword("Patient@123");
        request.setPhoneNumber("+201001234567");

        when(userRepository.existsByEmail("patient@hospital.com")).thenReturn(false);
        when(passwordEncoder.encode("Patient@123")).thenReturn("encoded-password");
        when(jwtService.generateToken(10L, Role.PATIENT)).thenReturn("patient-jwt-token");
        long fakeEpochMs = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.extractExpiration("patient-jwt-token")).thenReturn(fakeEpochMs);

        when(userRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setId(10L);
            return patient;
        });

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(10L, response.getUserId());
        assertEquals("patient@hospital.com", response.getEmail());
        assertEquals("patient-jwt-token", response.getToken());
        assertEquals(Role.PATIENT, response.getRole());
        assertTrue(response.getExpiresIn() > 3590L && response.getExpiresIn() <= 3600L);

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(userRepository).save(patientCaptor.capture());

        Patient savedPatient = patientCaptor.getValue();
        assertEquals("patient1", savedPatient.getUserName());
        assertEquals("Patient One", savedPatient.getFullName());
        assertEquals("patient@hospital.com", savedPatient.getEmail());
        assertEquals("encoded-password", savedPatient.getPassword());
        assertEquals("+201001234567", savedPatient.getPhoneNumber());

        verify(userRepository).existsByEmail("patient@hospital.com");
        verify(passwordEncoder).encode("Patient@123");
        verify(jwtService).generateToken(10L, Role.PATIENT);
        verify(jwtService).extractExpiration("patient-jwt-token");
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUserName("patient1");
        request.setFullName("Patient One");
        request.setEmail("patient@hospital.com");
        request.setPassword("Patient@123");

        when(userRepository.existsByEmail("patient@hospital.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email is already registered", ex.getReason());

        verify(userRepository).existsByEmail("patient@hospital.com");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateToken(anyLong(), any());
    }

    @Test
    void login_shouldReturnTokenAndRole_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@hospital.com");
        request.setPassword("Admin@1234");

        User user = User.builder()
                .id(1L)
                .userName("admin")
                .fullName("System Administrator")
                .email("admin@hospital.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("admin@hospital.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Admin@1234", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(1L, Role.ADMIN)).thenReturn("mock-jwt-token");
        long fakeEpochMs = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.extractExpiration("mock-jwt-token")).thenReturn(fakeEpochMs);

        LoginResponse response = authService.login(request);

        assertTrue(response.getExpiresIn() > 3590L && response.getExpiresIn() <= 3600L);
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(Role.ADMIN, response.getRole());

        verify(userRepository).findByEmail("admin@hospital.com");
        verify(passwordEncoder).matches("Admin@1234", "encoded-password");
        verify(jwtService).generateToken(1L, Role.ADMIN);
        verify(jwtService).extractExpiration("mock-jwt-token");
    }

    @Test
    void login_shouldThrowBadCredentials_whenUserIsNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@hospital.com");
        request.setPassword("Admin@1234");

        when(userRepository.findByEmail("missing@hospital.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail("missing@hospital.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyLong(), any());
    }

    @Test
    void login_shouldThrowBadCredentials_whenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@hospital.com");
        request.setPassword("wrong-password");

        User user = User.builder()
                .id(1L)
                .userName("admin")
                .fullName("System Administrator")
                .email("admin@hospital.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("admin@hospital.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail("admin@hospital.com");
        verify(passwordEncoder).matches("wrong-password", "encoded-password");
        verify(jwtService, never()).generateToken(anyLong(), any());
    }
}
