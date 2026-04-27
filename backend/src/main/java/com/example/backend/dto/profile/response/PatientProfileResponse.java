package com.example.backend.dto.profile.response;

import com.example.backend.enums.ChronicDisease;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse extends UserProfileResponse {
    private String emergencyNumber;
    private String whatsappNumber;
    private String bloodType;
    private ChronicDisease chronicDisease;
}
