package com.example.backend.mapper;

import com.example.backend.dto.profile.response.UserProfileResponse;
import com.example.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePicturePath(user.getProfilePicturePath())
                .createdAt(user.getCreatedAt())
                .build();
    }
}