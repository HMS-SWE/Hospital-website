package com.example.backend.dto;

import com.example.backend.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Register response containing created user info and auth tokens")
public class RegisterResponse {

    private Long userId;
    private String email;

    @Schema(description = "Short-lived JWT access token")
    private String accessToken;

    @Schema(description = "Long-lived refresh token")
    private String refreshToken;

    private Role role;
    private long expiresIn;
}
