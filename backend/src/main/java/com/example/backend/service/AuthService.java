package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

        Patient patient = Patient.builder()
                .userName(request.getUserName())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();

        Patient savedPatient = (Patient) userRepository.save(patient);

        String token = jwtService.generateToken(savedPatient.getId(), Role.PATIENT);
        long expiresIn = (jwtService.extractExpiration(token) - System.currentTimeMillis()) / 1000;

        return new RegisterResponse(
                savedPatient.getId(),
                savedPatient.getEmail(),
                token,
                Role.PATIENT,
                expiresIn
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());
        long expiresIn = (jwtService.extractExpiration(token) - System.currentTimeMillis()) / 1000;

        return new LoginResponse(token, user.getRole(), expiresIn);
    }
}
