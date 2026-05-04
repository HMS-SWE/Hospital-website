package com.example.backend.dto;

import com.example.backend.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Registration response containing JWT token and created user details")
public class RegisterResponse {

    @Schema(description = "Created user id", example = "12")
    private Long userId;

    @Schema(description = "Created user email", example = "mohamed@hospital.com")
    private String email;

    @Schema(
            description = "JWT access token used in Authorization header as Bearer token",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMiwicm9sZSI6IlBBVElFTlQiLCJpYXQiOjE3MTQyMzAwMDAsImV4cCI6MTcxNDIzMzYwMH0.signature"
    )
    private String token;

    @Schema(description = "Registered user's role", example = "PATIENT")
    private Role role;

    @Schema(description = "Token expiration in seconds", example = "3600")
    private long expiresIn;
}
