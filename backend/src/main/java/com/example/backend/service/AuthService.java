package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        String userName = request.getEmail().trim().split("@")[0];
        if (userRepository.existsByUserName(userName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        if (!"patient".equalsIgnoreCase(request.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only patient registration is allowed");
        }

        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender");
        }

        String fullName = String.join(" ",
                request.getFirstName().trim(),
                request.getMiddleName().trim(),
                request.getLastName().trim());

        Patient patient = Patient.builder()
                .userName(userName)
                .fullName(fullName)
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(gender)
                .birthDate(LocalDate.parse(request.getDob()))
                .phoneNumber(request.getPhone().trim())
                .emergencyNumber(request.getEmergency().trim())
                .nationalId(request.getNationalId().trim())
                .build();

        Patient savedPatient = (Patient) userRepository.save(patient);

        String accessToken = jwtService.generateAccessToken(savedPatient.getId(), Role.PATIENT);
        String refreshToken = jwtService.generateRefreshToken(savedPatient.getId(), Role.PATIENT);
        long expiresIn = (jwtService.extractExpiration(accessToken) - System.currentTimeMillis()) / 1000;

        return new RegisterResponse(
                savedPatient.getId(),
                savedPatient.getEmail(),
                accessToken,
                refreshToken,
                Role.PATIENT,
                expiresIn
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
        long expiresIn = (jwtService.extractExpiration(accessToken) - System.currentTimeMillis()) / 1000;

        return new LoginResponse(accessToken, refreshToken, user.getRole(), expiresIn);
    }

    public LoginResponse refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
        long expiresIn = (jwtService.extractExpiration(newAccessToken) - System.currentTimeMillis()) / 1000;

        return new LoginResponse(newAccessToken, newRefreshToken, user.getRole(), expiresIn);
    }
}
