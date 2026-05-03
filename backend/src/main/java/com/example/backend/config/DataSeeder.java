package com.example.backend.config;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

   
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("Admin already exists — skipping seed");
            return;
        }
        try {
            User admin = User.builder()
                    .userName("Admin")
                    .createdAt(java.time.LocalDateTime.now())
                    .updatedAt(java.time.LocalDateTime.now())
                    .fullName("System Admin")
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .isActive(true)
                    .role(Role.ADMIN)
                    .gender(null)
                    .build();
            userRepository.save(admin);
            log.info("Admin account created successfully");
        } catch (Exception e) {
            log.warn("DataSeeder skipped: {}", e.getMessage());
        }

    }
}