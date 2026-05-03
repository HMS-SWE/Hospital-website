package com.example.backend.dto;

import com.example.backend.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Login response containing access token and refresh token")
public class LoginResponse {

    @Schema(description = "Short-lived JWT access token")
    private String accessToken;

    @Schema(description = "Long-lived refresh token")
    private String refreshToken;

    @Schema(description = "Authenticated user's role", example = "DOCTOR")
    private Role role;

    @Schema(description = "Access token expiry in seconds", example = "900")
    private long expiresIn;
}
