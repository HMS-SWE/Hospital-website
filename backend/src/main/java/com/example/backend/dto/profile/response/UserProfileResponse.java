package com.example.backend.dto.profile.response;

import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String userName;
    private String fullName;
    private String email;
    private Role role;
    private Gender gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
    private String profilePicturePath;
    private LocalDateTime createdAt;
}
