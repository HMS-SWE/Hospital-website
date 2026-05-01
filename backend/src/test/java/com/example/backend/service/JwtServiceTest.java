package com.example.backend.service;

import com.example.backend.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "this-is-a-very-secure-secret-key-123456789";
    private static final long VALID_EXPIRATION_MS = 60_000;
    private static final long EXPIRED_EXPIRATION_MS = -1_000;

    @Test
    void generateToken_shouldReturnValidToken() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);

        String token = jwtService.generateToken(1L, Role.DOCTOR);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);
        String token = jwtService.generateToken(25L, Role.PATIENT);

        Long userId = jwtService.extractUserId(token);

        assertEquals(25L, userId);
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);
        String token = jwtService.generateToken(7L, Role.ADMIN);

        Role role = jwtService.extractRole(token);

        assertEquals(Role.ADMIN, role);
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);
        String token = jwtService.generateToken(3L, Role.DOCTOR);
        String tamperedToken = token + "abc";

        boolean isValid = jwtService.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, EXPIRED_EXPIRATION_MS);
        String token = jwtService.generateToken(9L, Role.PATIENT);

        boolean isValid = jwtService.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void extractUserId_shouldThrowExceptionForInvalidToken() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);

        assertThrows(Exception.class, () -> jwtService.extractUserId("invalid-token"));
    }

    @Test
    void extractRole_shouldThrowExceptionForInvalidToken() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);

        assertThrows(Exception.class, () -> jwtService.extractRole("invalid-token"));
    }
    @Test
    void generateToken_shouldPreserveRoleClaimForSpringSecurityMapping() {
        JwtService jwtService = new JwtService(SECRET, VALID_EXPIRATION_MS);
        String token = jwtService.generateToken(11L, Role.DOCTOR);

        Role role = jwtService.extractRole(token);
        String authority = "ROLE_" + role.name();

        assertEquals(Role.DOCTOR, role);
        assertEquals("ROLE_DOCTOR", authority);
    }

}
