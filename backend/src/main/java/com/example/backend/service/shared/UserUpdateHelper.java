package com.example.backend.service.shared;

import com.example.backend.dto.profile.request.UserProfileRequest;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void applyUpdates(User user, UserProfileRequest request) {
        applyBasicFields(user, request);
        applyPasswordChange(user, request);
    }

    private void applyBasicFields(User user, UserProfileRequest request) {
        // only query DB if the username actually changed
        if (!user.getUserName().equals(request.getUserName())) {
            if (userRepository.existsByUserName(request.getUserName())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUserName(request.getUserName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
    }

    private void applyPasswordChange(User user, UserProfileRequest request) {
        boolean wantsToChangePassword =
                request.getNewPassword() != null &&
                request.getCurrentPassword() != null;

        if (!wantsToChangePassword) return;

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
    }
}