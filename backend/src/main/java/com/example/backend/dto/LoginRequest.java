package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request payload")
public class LoginRequest {

    @Schema(description = "User email address", example = "doctor@hospital.com")
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "P@ssw0rd123")
    @NotBlank(message = "Password is required")
    private String password;
}
