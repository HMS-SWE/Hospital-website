package com.example.backend.service;

import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomOAuth2UserService service;

    @BeforeEach
    void setUp() {
        service = new CustomOAuth2UserService(userRepository);
    }

    @Test
    void shouldCreateNewPatient_whenUserDoesNotExist() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        service.processUser("test@gmail.com", "Test User", "https://pic.url");

        verify(userRepository).findByEmail("test@gmail.com");
        verify(userRepository).save(any(Patient.class));
    }

    @Test
    void shouldNotCreateNewPatient_whenUserAlreadyExists() {
        User existingUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .fullName("Test User")
                .role(Role.PATIENT)
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(existingUser));

        service.processUser("test@gmail.com", "Test User", "https://pic.url");

        verify(userRepository).findByEmail("test@gmail.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenEmailIsNull() {
        assertThrows(RuntimeException.class, () -> service.processUser(null, "Test User", "https://pic.url"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenEmailIsEmpty() {
        assertThrows(RuntimeException.class, () -> service.processUser("", "Test User", "https://pic.url"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUseEmailPrefix_whenFullNameIsNull() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        service.processUser("test@gmail.com", null, null);

        verify(userRepository).save(argThat(user -> ((Patient) user).getFullName().equals("test")));
    }

    @Test
    void shouldUseEmailPrefix_whenFullNameIsEmpty() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        service.processUser("test@gmail.com", "", null);

        verify(userRepository).save(argThat(user -> ((Patient) user).getFullName().equals("test")));
    }

    @Test
    void shouldSavePatientWithCorrectData_whenUserDoesNotExist() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        service.processUser("test@gmail.com", "Test User", "https://pic.url");

        verify(userRepository).save(argThat(user -> {
            Patient patient = (Patient) user;
            return patient.getEmail().equals("test@gmail.com")
                    && patient.getFullName().equals("Test User")
                    && patient.getUserName().equals("test")
                    && patient.getProfilePicturePath().equals("https://pic.url")
                    && patient.getPassword() == null;
        }));
    }
}