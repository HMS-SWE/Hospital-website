package com.example.backend.dto;

import com.example.backend.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Patient registration request payload")
public class RegisterRequest {

    @Schema(
            description = "Unique username",
            example = "mohamed_adel",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Username is required")
    private String userName;

    @Schema(
            description = "Full name",
            example = "Mohamed Adel",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Schema(
            description = "User email address",
            example = "mohamed@hospital.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "Account password",
            example = "Patient@123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;

    @Schema(
            description = "Phone number",
            example = "+201001234567"
    )
    private String phoneNumber;
}
