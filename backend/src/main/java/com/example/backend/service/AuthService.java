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
