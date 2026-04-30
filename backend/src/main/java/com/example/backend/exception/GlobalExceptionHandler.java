package com.example.backend.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.error.ValidationErrorResponse;

import java.util.Map;
//BadCredentialsException may bubble up as an internal error depending on setup
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
}
