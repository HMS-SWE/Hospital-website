package com.example.backend.exception;

import com.example.backend.dto.error.ValidationErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ── BadCredentialsException ──────────────────────────────────────────

    @Test
    @DisplayName("handleBadCredentials → 401 with message")
    void handleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<Map<String, String>> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("message", "Invalid credentials");
    }

    // ── MethodArgumentNotValidException ──────────────────────────────────

    @Test
    @DisplayName("handleValidationErrors → 400 with field errors list")
    void handleValidationErrors() {
        FieldError fieldError = new FieldError("obj", "diagnosis", "Diagnosis cannot be empty");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("diagnosis");
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Diagnosis cannot be empty");
    }

    @Test
    @DisplayName("handleValidationErrors → 400 with multiple field errors")
    void handleValidationErrorsMultipleFields() {
        FieldError error1 = new FieldError("obj", "diagnosis", "Diagnosis cannot be empty");
        FieldError error2 = new FieldError("obj", "treatmentPlan", "Treatment plan cannot be empty");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationErrors(ex);

        assertThat(response.getBody().getErrors()).hasSize(2);
    }

    // ── ResourceNotFoundException ─────────────────────────────────────────

    @Test
    @DisplayName("handleNotFound → 404 with general field error")
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Patient not found");

        ResponseEntity<ValidationErrorResponse> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("general");
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Patient not found");
    }

    // ── AccessDeniedException ─────────────────────────────────────────────

    @Test
    @DisplayName("handleAccessDenied → 403 with message")
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<Map<String, String>> response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "Access denied");
    }

    // ── RuntimeException ──────────────────────────────────────────────────

    @Test
    @DisplayName("handleRuntimeException → 400 with general field error")
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ResponseEntity<ValidationErrorResponse> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Something went wrong");
    }

    // ── ConflictException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleConflict → 409 with general field error")
    void handleConflict() {
        ConflictException ex = new ConflictException("Username already taken");

        ResponseEntity<ValidationErrorResponse> response = handler.handleConflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Username already taken");
    }

    // ── ResponseStatusException ───────────────────────────────────────────

    @Test
    @DisplayName("handleResponseStatusException → status from exception with reason")
    void handleResponseStatusExceptionWithReason() {
        ResponseStatusException ex = new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");

        ResponseEntity<Map<String, String>> response = handler.handleResponseStatusException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).containsEntry("message", "Rate limit exceeded");
    }

    @Test
    @DisplayName("handleResponseStatusException → fallback message when reason is null")
    void handleResponseStatusExceptionNullReason() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_GATEWAY);

        ResponseEntity<Map<String, String>> response = handler.handleResponseStatusException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).containsEntry("message", "Request failed");
    }

    // ── Exception subclasses ─────────────────────────────────────────────

    @Test
    @DisplayName("ConflictException inherits message from RuntimeException")
    void conflictExceptionMessage() {
        ConflictException ex = new ConflictException("Duplicate entry");
        assertThat(ex.getMessage()).isEqualTo("Duplicate entry");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("ResourceNotFoundException inherits message from RuntimeException")
    void resourceNotFoundExceptionMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertThat(ex.getMessage()).isEqualTo("Not found");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}