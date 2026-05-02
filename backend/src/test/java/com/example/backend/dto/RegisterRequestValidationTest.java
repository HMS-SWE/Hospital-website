package com.example.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void password_shouldFail_whenTooShort() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("Aa1!");

        assertHasMessage(violations, "Password must be at least 8 characters long");
    }

    @Test
    void password_shouldFail_whenMissingUppercase() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("patient@123");

        assertHasMessage(violations, "Password must contain at least one uppercase letter");
    }

    @Test
    void password_shouldFail_whenMissingLowercase() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("PATIENT@123");

        assertHasMessage(violations, "Password must contain at least one lowercase letter");
    }

    @Test
    void password_shouldFail_whenMissingNumber() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("Patient@abc");

        assertHasMessage(violations, "Password must contain at least one number");
    }

    @Test
    void password_shouldFail_whenMissingSpecialCharacter() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("Patient123");

        assertHasMessage(violations, "Password must contain at least one special character");
    }

    @Test
    void password_shouldFail_whenEmpty() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("");

        assertHasMessage(violations, "Password is required");
    }

    @Test
    void password_shouldFail_whenOnlySpaces() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("        ");

        assertHasMessage(violations, "Password is required");
    }

    @Test
    void password_shouldPass_whenStrongPasswordIsProvided() {
        Set<ConstraintViolation<RegisterRequest>> violations = validatePassword("Patient@123");

        assertTrue(
                violations.stream().noneMatch(v -> "password".equals(v.getPropertyPath().toString())),
                "Expected no password validation violations for a strong password"
        );
    }

    private Set<ConstraintViolation<RegisterRequest>> validatePassword(String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUserName("patient1");
        request.setFullName("Patient One");
        request.setEmail("patient@hospital.com");
        request.setPassword(password);
        request.setPhoneNumber("+201001234567");

        return validator.validate(request);
    }

    private void assertHasMessage(Set<ConstraintViolation<RegisterRequest>> violations, String expectedMessage) {
        assertTrue(
                violations.stream().anyMatch(v -> expectedMessage.equals(v.getMessage())),
                "Expected violation message not found: " + expectedMessage
        );
    }
}
