package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
