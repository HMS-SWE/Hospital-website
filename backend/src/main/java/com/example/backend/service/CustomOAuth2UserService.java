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
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String picture = oidcUser.getPicture();

        log.info("OAuth2 login attempt for email: {}", email);

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

        return oidcUser;
    }
}