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
    void register_shouldCreatePatientAndReturnTokens_whenRequestIsValid() {
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

        when(userRepository.existsByEmail("patient@hospital.com")).thenReturn(false);
        when(userRepository.existsByUserName("patient")).thenReturn(false);
        when(passwordEncoder.encode("Patient@123")).thenReturn("encoded-password");
        when(jwtService.generateAccessToken(10L, Role.PATIENT)).thenReturn("patient-access-token");
        when(jwtService.generateRefreshToken(10L, Role.PATIENT)).thenReturn("patient-refresh-token");
        long fakeEpochMs = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.extractExpiration("patient-access-token")).thenReturn(fakeEpochMs);

        when(userRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setId(10L);
            return patient;
        });

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(10L, response.getUserId());
        assertEquals("patient@hospital.com", response.getEmail());
        assertEquals("patient-access-token", response.getAccessToken());
        assertEquals("patient-refresh-token", response.getRefreshToken());
        assertEquals(Role.PATIENT, response.getRole());
        assertTrue(response.getExpiresIn() > 3590L && response.getExpiresIn() <= 3600L);

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(userRepository).save(patientCaptor.capture());

        Patient savedPatient = patientCaptor.getValue();
        assertEquals("patient", savedPatient.getUserName());
        assertEquals("Patient One Test", savedPatient.getFullName());
        assertEquals("patient@hospital.com", savedPatient.getEmail());
        assertEquals("encoded-password", savedPatient.getPassword());
        assertEquals("+201001234567", savedPatient.getPhoneNumber());

        verify(jwtService).generateAccessToken(10L, Role.PATIENT);
        verify(jwtService).generateRefreshToken(10L, Role.PATIENT);
        verify(jwtService).extractExpiration("patient-access-token");
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("patient@hospital.com");

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
        verify(jwtService, never()).generateAccessToken(anyLong(), any());
        verify(jwtService, never()).generateRefreshToken(anyLong(), any());
    }

    @Test
    void login_shouldReturnTokensAndRole_whenCredentialsAreValid() {
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
        when(jwtService.generateAccessToken(1L, Role.ADMIN)).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(1L, Role.ADMIN)).thenReturn("mock-refresh-token");
        long fakeEpochMs = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.extractExpiration("mock-access-token")).thenReturn(fakeEpochMs);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals(Role.ADMIN, response.getRole());
        assertTrue(response.getExpiresIn() > 3590L && response.getExpiresIn() <= 3600L);

        verify(userRepository).findByEmail("admin@hospital.com");
        verify(passwordEncoder).matches("Admin@1234", "encoded-password");
        verify(jwtService).generateAccessToken(1L, Role.ADMIN);
        verify(jwtService).generateRefreshToken(1L, Role.ADMIN);
        verify(jwtService).extractExpiration("mock-access-token");
    }

    @Test
    void refresh_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        User user = User.builder()
                .id(1L)
                .userName("admin")
                .fullName("System Administrator")
                .email("admin@hospital.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();

        when(jwtService.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtService.isRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-refresh-token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(1L, Role.ADMIN)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(1L, Role.ADMIN)).thenReturn("new-refresh-token");
        long fakeEpochMs = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.extractExpiration("new-access-token")).thenReturn(fakeEpochMs);

        LoginResponse response = authService.refresh("valid-refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(Role.ADMIN, response.getRole());
        assertTrue(response.getExpiresIn() > 3590L && response.getExpiresIn() <= 3600L);
    }

    @Test
    void refresh_shouldThrowBadCredentials_whenTokenIsInvalid() {
        when(jwtService.validateToken("bad-refresh-token")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.refresh("bad-refresh-token"));

        verify(jwtService).validateToken("bad-refresh-token");
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void refresh_shouldThrowBadCredentials_whenTokenIsNotRefreshToken() {
        when(jwtService.validateToken("access-token")).thenReturn(true);
        when(jwtService.isRefreshToken("access-token")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.refresh("access-token"));

        verify(jwtService).validateToken("access-token");
        verify(jwtService).isRefreshToken("access-token");
        verify(userRepository, never()).findById(anyLong());
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
        verify(jwtService, never()).generateAccessToken(anyLong(), any());
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
        verify(jwtService, never()).generateAccessToken(anyLong(), any());
    }
}
