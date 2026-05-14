package com.example.backend.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.error.ValidationErrorResponse;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

     // ← @Valid fails — missing fields, wrong format, etc. → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();

        List<ValidationErrorResponse.FieldError> fieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(error -> ValidationErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.badRequest().body(
                ValidationErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(fieldErrors)
                        .build()
        );
    }

    // ← user/doctor/patient not found → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ValidationErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .errors(List.of(
                                ValidationErrorResponse.FieldError.builder()
                                        .field("general")
                                        .message(ex.getMessage())
                                        .build()
                        ))
                        .build()
        );
    }

    // ← access denied (wrong role, not assigned doctor, etc.) → 403
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    // ← username taken, wrong password, etc. → 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ValidationErrorResponse> handleRuntimeException(
            RuntimeException ex) {

        return ResponseEntity.badRequest().body(
                ValidationErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(List.of(
                                ValidationErrorResponse.FieldError.builder()
                                        .field("general")
                                        .message(ex.getMessage())
                                        .build()
                        ))
                        .build()
        );
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ValidationErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ValidationErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .errors(List.of(
                                ValidationErrorResponse.FieldError.builder()
                                        .field("general")
                                        .message(ex.getMessage())
                                        .build()
                        ))
                        .build()
        );
        }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Request failed";
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", message));
    }
}
