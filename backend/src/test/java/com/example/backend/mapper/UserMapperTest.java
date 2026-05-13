package com.example.backend.mapper;

import com.example.backend.dto.profile.response.UserProfileResponse;
import com.example.backend.entity.User;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("maps all User fields to UserProfileResponse correctly")
    void mapsAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDate birthDate = LocalDate.of(1990, 5, 20);

        User user = User.builder()
                .userName("johndoe")
                .fullName("John Doe")
                .email("john@example.com")
                .role(Role.ADMIN)
                .gender(Gender.MALE)
                .birthDate(birthDate)
                .phoneNumber("01012345678")
                .address("123 Main St")
                .profilePicturePath("/images/john.jpg")
                .build();

        // set id and createdAt via reflection (managed by BaseEntity / JPA auditing)
        setField(user, "id", 1L);
        setField(user, "createdAt", now);

        UserProfileResponse response = userMapper.toResponse(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserName()).isEqualTo("johndoe");
        assertThat(response.getFullName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        assertThat(response.getGender()).isEqualTo(Gender.MALE);
        assertThat(response.getBirthDate()).isEqualTo(birthDate);
        assertThat(response.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(response.getAddress()).isEqualTo("123 Main St");
        assertThat(response.getProfilePicturePath()).isEqualTo("/images/john.jpg");
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("maps null optional fields without throwing")
    void mapsNullOptionalFields() {
        User user = User.builder()
                .userName("minimal")
                .fullName("Minimal User")
                .email("minimal@example.com")
                .build();

        UserProfileResponse response = userMapper.toResponse(user);

        assertThat(response.getUserName()).isEqualTo("minimal");
        assertThat(response.getFullName()).isEqualTo("Minimal User");
        assertThat(response.getEmail()).isEqualTo("minimal@example.com");
        assertThat(response.getGender()).isNull();
        assertThat(response.getBirthDate()).isNull();
        assertThat(response.getPhoneNumber()).isNull();
        assertThat(response.getAddress()).isNull();
        assertThat(response.getProfilePicturePath()).isNull();
        assertThat(response.getCreatedAt()).isNull();
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private void setField(Object target, String fieldName, Object value) {
        try {
            // walk up the hierarchy to find the field
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new RuntimeException("Field not found: " + fieldName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}