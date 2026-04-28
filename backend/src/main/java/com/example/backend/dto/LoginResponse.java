package com.example.backend.dto;

import com.example.backend.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Login response containing the JWT token and user role")
public class LoginResponse {

    @Schema(
            description = "JWT access token used in Authorization header as Bearer token",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkRPQ1RPUiIsImlhdCI6MTcxNDIzMDAwMCwiZXhwIjoxNzE0MjMzNjAwfQ.signature"
    )
    private String token;

    @Schema(
            description = "Authenticated user's role",
            example = "DOCTOR"
    )
    private Role role;
    private long expiresIn;
}
