package com.example.backend.dto.error;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ValidationErrorResponse {
    private int status;
    private List<FieldError> errors;

    @Getter
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}