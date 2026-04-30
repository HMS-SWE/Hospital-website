package com.example.backend.dto.profile.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;


class UserProfileRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void valid_request_passes() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .email("john@hospital.com")
                .phoneNumber("+201234567890")
                .build();

        Set<ConstraintViolation<UserProfileRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();  // no violations = valid DTO
    }

    @Test
    void blank_username_fails() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("")              // ← violates @NotBlank
                .fullName("John Doe")
                .build();

        Set<ConstraintViolation<UserProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("userName"));
    }

    @Test
    void invalid_email_fails() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .email("not-an-email")
                .build();

        Set<ConstraintViolation<UserProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void weak_password_fails() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .currentPassword("current")
                .newPassword("weakpassword")  
                .build();

        Set<ConstraintViolation<UserProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }

    @Test
    void violation_message_is_correct() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("")
                .fullName("John Doe")
                .build();

        Set<ConstraintViolation<UserProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals("Username is required"));
    }
}