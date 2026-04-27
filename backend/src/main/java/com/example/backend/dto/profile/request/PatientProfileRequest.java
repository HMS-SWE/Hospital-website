package com.example.backend.dto.profile.request;

import com.example.backend.enums.ChronicDisease;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileRequest extends UserProfileRequest {
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid emergency number format")
    private String emergencyNumber;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid WhatsApp number format")
    private String whatsappNumber;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood type")
    private String bloodType;

    private ChronicDisease chronicDisease;
}
