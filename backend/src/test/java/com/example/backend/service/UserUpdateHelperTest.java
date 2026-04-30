package com.example.backend.service;

import com.example.backend.dto.profile.request.UserProfileRequest;
import com.example.backend.entity.User;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.shared.UserUpdateHelper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUpdateHelperTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserUpdateHelper userUpdateHelper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .userName("john")
                .fullName("John Doe")
                .email("john@hospital.com")
                .password("encodedOldPassword")
                .role(Role.ADMIN)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("+201234567890")
                .address("123 Main St")
                .build();
        mockUser.setId(1L);
    }

    // =========================================================================
    // BASIC FIELDS
    // =========================================================================

    @Test
    @DisplayName("applyUpdates → updates basic fields correctly")
    void applyUpdates_updatesBasicFields() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john_updated")
                .fullName("John Doe Updated")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("+201111111111")
                .address("456 New St")
                .build();

        userUpdateHelper.applyUpdates(mockUser, request);

        assertThat(mockUser.getUserName()).isEqualTo("john_updated");
        assertThat(mockUser.getFullName()).isEqualTo("John Doe Updated");
        assertThat(mockUser.getPhoneNumber()).isEqualTo("+201111111111");
        assertThat(mockUser.getAddress()).isEqualTo("456 New St");
    }

    // =========================================================================
    // EMAIL CHANGE
    // =========================================================================

    @Test
    @DisplayName("applyUpdates → updates email when new email is not taken")
    void applyUpdates_updatesEmail_whenNotTaken() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .email("newemail@hospital.com")
                .build();

        when(userRepository.existsByEmail("newemail@hospital.com")).thenReturn(false);

        userUpdateHelper.applyUpdates(mockUser, request);

        assertThat(mockUser.getEmail()).isEqualTo("newemail@hospital.com");
        verify(userRepository, times(1)).existsByEmail("newemail@hospital.com");
    }

    @Test
    @DisplayName("applyUpdates → skips duplicate check when email unchanged")
    void applyUpdates_skipsDuplicateCheck_whenEmailUnchanged() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .email("john@hospital.com")
                .build();

        userUpdateHelper.applyUpdates(mockUser, request);

        // existsByEmail should never be called if email didn't change
        verify(userRepository, never()).existsByEmail(any());
        assertThat(mockUser.getEmail()).isEqualTo("john@hospital.com");
    }

    @Test
    @DisplayName("applyUpdates → throws when new email is already taken")
    void applyUpdates_throws_whenEmailAlreadyTaken() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .email("taken@hospital.com")
                .build();

        when(userRepository.existsByEmail("taken@hospital.com")).thenReturn(true);

        assertThatThrownBy(() -> userUpdateHelper.applyUpdates(mockUser, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already in use");

        // email on entity should not have changed
        assertThat(mockUser.getEmail()).isEqualTo("john@hospital.com");
    }

    // =========================================================================
    // PASSWORD CHANGE
    // =========================================================================

    @Test
    @DisplayName("applyUpdates → changes password and stamps passwordChangedAt")
    void applyUpdates_changesPassword_andStampsTimestamp() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .currentPassword("oldPassword")
                .newPassword("NewPass@123")
                .build();

        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("encodedNewPassword");

        LocalDateTime before = LocalDateTime.now();
        userUpdateHelper.applyUpdates(mockUser, request);
        LocalDateTime after = LocalDateTime.now();

        // password was changed
        assertThat(mockUser.getPassword()).isEqualTo("encodedNewPassword");

        // passwordChangedAt was stamped — and it's between before and after
        assertThat(mockUser.getPasswordChangedAt()).isNotNull();
        assertThat(mockUser.getPasswordChangedAt()).isAfterOrEqualTo(before);
        assertThat(mockUser.getPasswordChangedAt()).isBeforeOrEqualTo(after);
    }

    @Test
    @DisplayName("applyUpdates → throws when current password is wrong")
    void applyUpdates_throws_whenCurrentPasswordWrong() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .currentPassword("wrongPassword")
                .newPassword("NewPass@123")
                .build();

        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        assertThatThrownBy(() -> userUpdateHelper.applyUpdates(mockUser, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Current password is incorrect");

        // password and timestamp should be unchanged
        assertThat(mockUser.getPassword()).isEqualTo("encodedOldPassword");
        assertThat(mockUser.getPasswordChangedAt()).isNull();
    }

    @Test
    @DisplayName("applyUpdates → skips password change when fields are null")
    void applyUpdates_skipsPasswordChange_whenFieldsNull() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .currentPassword(null)   // not trying to change password
                .newPassword(null)
                .build();

        userUpdateHelper.applyUpdates(mockUser, request);

        // encoder should never be called
        verify(passwordEncoder, never()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());

        // password and timestamp unchanged
        assertThat(mockUser.getPassword()).isEqualTo("encodedOldPassword");
        assertThat(mockUser.getPasswordChangedAt()).isNull();
    }

    @Test
    @DisplayName("applyUpdates → skips password change when only one field provided")
    void applyUpdates_skipsPasswordChange_whenOnlyOneFieldProvided() {
        UserProfileRequest request = UserProfileRequest.builder()
                .userName("john")
                .fullName("John Doe")
                .currentPassword("oldPassword")
                .newPassword(null)           // missing newPassword
                .build();

        userUpdateHelper.applyUpdates(mockUser, request);

        verify(passwordEncoder, never()).matches(any(), any());
        assertThat(mockUser.getPasswordChangedAt()).isNull();
    }
}