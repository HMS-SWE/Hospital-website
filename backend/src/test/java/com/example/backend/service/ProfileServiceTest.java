package com.example.backend.service;

import com.example.backend.dto.profile.request.*;
import com.example.backend.dto.profile.response.*;
import com.example.backend.entity.*;
import com.example.backend.enums.*;
import com.example.backend.mapper.*;
import com.example.backend.repository.*;
import com.example.backend.service.shared.UserUpdateHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

        // ─── Mocks ───────────────────────────────────────────────────────────────
        @Mock
        private UserRepository userRepository;
        @Mock
        private DoctorRepository doctorRepository;
        @Mock
        private PatientRepository patientRepository;
        @Mock
        private UserMapper userMapper;
        @Mock
        private DoctorMapper doctorMapper;
        @Mock
        private PatientMapper patientMapper;
        @Mock
        private UserUpdateHelper userUpdateHelper;

        @InjectMocks
        private ProfileService profileService;

        // ─── Shared test data ─────────────────────────────────────────────────────
        private User mockUser;
        private Doctor mockDoctor;
        private Patient mockPatient;
        private Specialty mockSpecialty;

        private UserProfileResponse mockUserResponse;
        private DoctorProfileResponse mockDoctorResponse;
        private PatientProfileResponse mockPatientResponse;

        @BeforeEach
        void setUp() {
                // ── User ──
                mockUser = User.builder()
                                .userName("john")
                                .fullName("John Doe")
                                .email("john@hospital.com")
                                .password("encodedPassword")
                                .role(Role.ADMIN)
                                .gender(Gender.MALE)
                                .birthDate(LocalDate.of(1990, 1, 1))
                                .phoneNumber("+201234567890")
                                .address("123 Main St")
                                .build();
                mockUser.setId(1L);

                // ── Specialty ──
                mockSpecialty = Specialty.builder()
                                .name("Cardiology")
                                .location("Building A")
                                .build();
                mockSpecialty.setId(1L);

                // ── Doctor ──
                mockDoctor = Doctor.builder()
                                .userName("drsmith")
                                .fullName("Dr. Smith")
                                .email("smith@hospital.com")
                                .password("encodedPassword")
                                .role(Role.DOCTOR)
                                .specialty(mockSpecialty)
                                .department("Cardiology Dept")
                                .degree("MD")
                                .licenseNumber("LIC-001")
                                .examinationPrice(200.0f)
                                .build();
                mockDoctor.setId(2L);

                // ── Patient ──
                mockPatient = Patient.builder()
                                .userName("patient1")
                                .fullName("Jane Doe")
                                .email("jane@hospital.com")
                                .password("encodedPassword")
                                .role(Role.PATIENT)
                                .bloodType("A+")
                                .emergencyNumber("+201111111111")
                                .chronicDisease(ChronicDisease.DIABETES)
                                .build();
                mockPatient.setId(3L);

                // ── Responses ──
                mockUserResponse = UserProfileResponse.builder()
                                .id(1L)
                                .userName("john")
                                .fullName("John Doe")
                                .email("john@hospital.com")
                                .role(Role.ADMIN)
                                .createdAt(LocalDateTime.now())
                                .build();

                mockDoctorResponse = DoctorProfileResponse.builder()
                                .id(2L)
                                .userName("drsmith")
                                .fullName("Dr. Smith")
                                .email("smith@hospital.com")
                                .specialtyName("Cardiology")
                                .specialtyLocation("Building A")
                                .department("Cardiology Dept")
                                .examinationPrice(200.0f)
                                .build();

                mockPatientResponse = PatientProfileResponse.builder()
                                .id(3L)
                                .userName("patient1")
                                .fullName("Jane Doe")
                                .email("jane@hospital.com")
                                .bloodType("A+")
                                .chronicDisease(ChronicDisease.DIABETES)
                                .build();
        }

        // =========================================================================
        // GET USER PROFILE
        // =========================================================================

        @Test
        @DisplayName("getUserProfile → returns correct response when user exists")
        void getUserProfile_returnsResponse_whenUserExists() {
                when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
                when(userMapper.toResponse(mockUser)).thenReturn(mockUserResponse);

                UserProfileResponse result = profileService.getUserProfile(1L);

                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1L);
                assertThat(result.getUserName()).isEqualTo("john");
                assertThat(result.getEmail()).isEqualTo("john@hospital.com");

                // verify the repo and mapper were each called exactly once
                verify(userRepository, times(1)).findById(1L);
                verify(userMapper, times(1)).toResponse(mockUser);
        }

        @Test
        @DisplayName("getUserProfile → throws RuntimeException when user not found")
        void getUserProfile_throwsException_whenUserNotFound() {
                when(userRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> profileService.getUserProfile(99L))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("User not found");

                // mapper should never be called if user doesn't exist
                verify(userMapper, never()).toResponse(any());
        }

        // =========================================================================
        // GET DOCTOR PROFILE
        // =========================================================================

        @Test
        @DisplayName("getDoctorProfile → returns correct response when doctor exists")
        void getDoctorProfile_returnsResponse_whenDoctorExists() {
                when(doctorRepository.findById(2L)).thenReturn(Optional.of(mockDoctor));
                when(doctorMapper.toResponse(mockDoctor)).thenReturn(mockDoctorResponse);

                DoctorProfileResponse result = profileService.getDoctorProfile(2L);

                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(2L);
                assertThat(result.getSpecialtyName()).isEqualTo("Cardiology");
                assertThat(result.getExaminationPrice()).isEqualTo(200.0f);

                verify(doctorRepository, times(1)).findById(2L);
                verify(doctorMapper, times(1)).toResponse(mockDoctor);
        }

        @Test
        @DisplayName("getDoctorProfile → throws RuntimeException when doctor not found")
        void getDoctorProfile_throwsException_whenDoctorNotFound() {
                when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> profileService.getDoctorProfile(99L))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("Doctor profile not found");

                verify(doctorMapper, never()).toResponse(any());
        }

        // =========================================================================
        // GET PATIENT PROFILE
        // =========================================================================

        @Test
        @DisplayName("getPatientProfile → returns correct response when patient exists")
        void getPatientProfile_returnsResponse_whenPatientExists() {
                when(patientRepository.findById(3L)).thenReturn(Optional.of(mockPatient));
                when(patientMapper.toResponse(mockPatient)).thenReturn(mockPatientResponse);

                PatientProfileResponse result = profileService.getPatientProfile(3L);

                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(3L);
                assertThat(result.getBloodType()).isEqualTo("A+");
                assertThat(result.getChronicDisease()).isEqualTo(ChronicDisease.DIABETES);

                verify(patientRepository, times(1)).findById(3L);
                verify(patientMapper, times(1)).toResponse(mockPatient);
        }

        @Test
        @DisplayName("getPatientProfile → throws RuntimeException when patient not found")
        void getPatientProfile_throwsException_whenPatientNotFound() {
                when(patientRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> profileService.getPatientProfile(99L))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("Patient profile not found");

                verify(patientMapper, never()).toResponse(any());
        }

        // =========================================================================
        // UPDATE USER PROFILE
        // =========================================================================

        @Test
        @DisplayName("updateUserProfile → saves and returns updated response")
        void updateUserProfile_savesAndReturnsResponse() {
                UserProfileRequest request = UserProfileRequest.builder()
                                .userName("john_updated")
                                .fullName("John Doe Updated")
                                .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
                when(userRepository.save(mockUser)).thenReturn(mockUser);
                when(userMapper.toResponse(mockUser)).thenReturn(mockUserResponse);

                UserProfileResponse result = profileService.updateUserProfile(1L, request);

                assertThat(result).isNotNull();

                // verify the helper was called to apply field updates
                verify(userUpdateHelper, times(1)).applyUpdates(mockUser, request);

                // verify save was called
                verify(userRepository, times(1)).save(mockUser);
        }

        @Test
        @DisplayName("updateUserProfile → throws when user not found")
        void updateUserProfile_throwsException_whenUserNotFound() {
                when(userRepository.findById(99L)).thenReturn(Optional.empty());

                UserProfileRequest request = UserProfileRequest.builder()
                                .userName("john")
                                .fullName("John Doe")
                                .build();

                assertThatThrownBy(() -> profileService.updateUserProfile(99L, request))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("User not found");

                // nothing else should be called
                verify(userUpdateHelper, never()).applyUpdates(any(), any());
                verify(userRepository, never()).save(any());
        }

        // =========================================================================
        // UPDATE DOCTOR PROFILE
        // =========================================================================

        @Test
        @DisplayName("updateDoctorProfile → updates doctor-specific fields and saves")
        void updateDoctorProfile_updatesFieldsAndSaves() {
                DoctorProfileRequest request = DoctorProfileRequest.builder()
                                .userName("drsmith")
                                .fullName("Dr. Smith")
                                .specialtyId(1L)
                                .department("New Department")
                                .degree("PhD")
                                .licenseNumber("LIC-002")
                                .examinationPrice(300.0f)
                                .build();

                when(doctorRepository.findById(2L)).thenReturn(Optional.of(mockDoctor));
                when(doctorRepository.save(mockDoctor)).thenReturn(mockDoctor);
                when(doctorMapper.toResponse(mockDoctor)).thenReturn(mockDoctorResponse);

                profileService.updateDoctorProfile(2L, request);

                // verify shared logic was applied
                verify(userUpdateHelper, times(1)).applyUpdates(mockDoctor, request);

                // verify doctor-specific fields were updated directly on entity
                assertThat(mockDoctor.getDepartment()).isEqualTo("New Department");
                assertThat(mockDoctor.getDegree()).isEqualTo("PhD");
                assertThat(mockDoctor.getLicenseNumber()).isEqualTo("LIC-002");
                assertThat(mockDoctor.getExaminationPrice()).isEqualTo(300.0f);

                verify(doctorRepository, times(1)).save(mockDoctor);
        }

        @Test
        @DisplayName("updateDoctorProfile → throws when doctor not found")
        void updateDoctorProfile_throwsException_whenDoctorNotFound() {
                when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

                DoctorProfileRequest request = DoctorProfileRequest.builder()
                                .userName("drsmith")
                                .fullName("Dr. Smith")
                                .specialtyId(1L)
                                .build();

                assertThatThrownBy(() -> profileService.updateDoctorProfile(99L, request))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("Doctor profile not found");

                verify(userUpdateHelper, never()).applyUpdates(any(), any());
                verify(doctorRepository, never()).save(any());
        }

        // =========================================================================
        // UPDATE PATIENT PROFILE
        // =========================================================================

        @Test
        @DisplayName("updatePatientProfile → updates patient-specific fields and saves")
        void updatePatientProfile_updatesFieldsAndSaves() {
                PatientProfileRequest request = PatientProfileRequest.builder()
                                .userName("patient1")
                                .fullName("Jane Doe")
                                .emergencyNumber("+202222222222")
                                .bloodType("B+")
                                .chronicDisease(ChronicDisease.BLOOD_PRESSURE)
                                .build();

                when(patientRepository.findById(3L)).thenReturn(Optional.of(mockPatient));
                when(patientRepository.save(mockPatient)).thenReturn(mockPatient);
                when(patientMapper.toResponse(mockPatient)).thenReturn(mockPatientResponse);

                profileService.updatePatientProfile(3L, request);

                verify(userUpdateHelper, times(1)).applyUpdates(mockPatient, request);

                assertThat(mockPatient.getEmergencyNumber()).isEqualTo("+202222222222");
                assertThat(mockPatient.getBloodType()).isEqualTo("B+");
                assertThat(mockPatient.getChronicDisease()).isEqualTo(ChronicDisease.BLOOD_PRESSURE);

                verify(patientRepository, times(1)).save(mockPatient);
        }

        @Test
        @DisplayName("updatePatientProfile → throws when patient not found")
        void updatePatientProfile_throwsException_whenPatientNotFound() {
                when(patientRepository.findById(99L)).thenReturn(Optional.empty());

                PatientProfileRequest request = PatientProfileRequest.builder()
                                .userName("patient1")
                                .fullName("Jane Doe")
                                .build();

                assertThatThrownBy(() -> profileService.updatePatientProfile(99L, request))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("Patient profile not found");

                verify(userUpdateHelper, never()).applyUpdates(any(), any());
                verify(patientRepository, never()).save(any());
        }
}