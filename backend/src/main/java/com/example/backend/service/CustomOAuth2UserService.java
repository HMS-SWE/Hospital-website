package com.example.backend.service;

import com.example.backend.entity.Patient;
import com.example.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("CustomOAuth2UserService bean created!");
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        log.info("loadUser called!");
        OidcUser oidcUser;

        try {
            oidcUser = super.loadUser(userRequest);
        } catch (Exception e) {
            log.error("Failed to load user from Google: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to Google: " + e.getMessage(), e);
        }

        processUser(oidcUser.getEmail(), oidcUser.getFullName(), oidcUser.getPicture());

        return oidcUser;
    }

    public void processUser(String email, String name, String picture) {
        if (email == null || email.isEmpty()) {
            log.error("Email not provided by Google");
            throw new RuntimeException("Email not provided by Google");
        }

        if (name == null || name.isEmpty()) {
            name = email.split("@")[0];
            log.warn("Full name not provided, using: {}", name);
        }

        try {
            if (userRepository.findByEmail(email).isEmpty()) {
                log.info("User not found, creating new patient for: {}", email);
                Patient newPatient = Patient.builder()
                        .email(email)
                        .fullName(name)
                        .userName(email.split("@")[0])
                        .password(null)
                        .profilePicturePath(picture)
                        .build();
                Patient saved = userRepository.save(newPatient);
                log.info("Patient saved successfully with id: {}", saved.getId());
            } else {
                log.info("User already exists for: {}", email);
            }
        } catch (Exception e) {
            log.error("Failed to save OAuth2 user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save OAuth2 user: " + e.getMessage(), e);
        }
    }
}