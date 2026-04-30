package com.example.backend.controller;

import com.example.backend.dto.profile.request.DoctorProfileRequest;
import com.example.backend.dto.profile.request.PatientProfileRequest;
import com.example.backend.dto.profile.response.DoctorProfileResponse;
import com.example.backend.dto.profile.response.PatientProfileResponse;
import com.example.backend.dto.profile.response.UserProfileResponse;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

        @Mock
        private ProfileService profileService;

        @Mock
        private HttpServletRequest request;

        @InjectMocks
        private ProfileController profileController;

        private final Long ADMIN_ID = 1L;
        private final Long DOCTOR_ID = 2L;
        private final Long PATIENT_ID = 3L;

        @BeforeEach
        void setUp() {
        }

        // ─── GET DOCTOR PROFILE ───────────────────────────────────────────────────

        @Test
        void getDoctorProfile_shouldReturnFullProfile_whenRequestingUserIsSelf() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(DOCTOR_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.DOCTOR);
                when(profileService.getDoctorProfile(DOCTOR_ID)).thenReturn(new DoctorProfileResponse());

                ResponseEntity<?> response = profileController.getDoctorProfile(DOCTOR_ID, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).getDoctorProfile(DOCTOR_ID);
        }

        // ─── GET PATIENT PROFILE ──────────────────────────────────────────────────

        @Test
        void getPatientProfile_shouldReturnProfile_whenRequestingUserIsAdmin() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(ADMIN_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.ADMIN);
                when(profileService.getPatientProfile(PATIENT_ID)).thenReturn(new PatientProfileResponse());

                ResponseEntity<PatientProfileResponse> response = profileController.getPatientProfile(PATIENT_ID,
                                request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).getPatientProfile(PATIENT_ID);
        }

        @Test
        void getPatientProfile_shouldReturnProfile_whenRequestingUserIsSelf() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(PATIENT_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.PATIENT);
                when(profileService.getPatientProfile(PATIENT_ID)).thenReturn(new PatientProfileResponse());

                ResponseEntity<PatientProfileResponse> response = profileController.getPatientProfile(PATIENT_ID,
                                request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).getPatientProfile(PATIENT_ID);
        }

        @Test
        void getPatientProfile_shouldReturnForbidden_whenRequestingUserIsNotAdminOrSelf() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(DOCTOR_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.DOCTOR);

                ResponseEntity<PatientProfileResponse> response = profileController.getPatientProfile(PATIENT_ID,
                                request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).getPatientProfile(any());
        }

        // ─── GET USER PROFILE ─────────────────────────────────────────────────────

        @Test
        void getUserProfile_shouldReturnProfile_whenRequestingUserIsAdmin() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(ADMIN_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.ADMIN);
                when(profileService.getUserProfile(PATIENT_ID)).thenReturn(new UserProfileResponse());

                ResponseEntity<UserProfileResponse> response = profileController.getUserProfile(PATIENT_ID, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).getUserProfile(PATIENT_ID);
        }

        @Test
        void getUserProfile_shouldReturnForbidden_whenRequestingUserIsNotAdminOrSelf() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(DOCTOR_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.DOCTOR);

                ResponseEntity<UserProfileResponse> response = profileController.getUserProfile(PATIENT_ID, request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).getUserProfile(any());
        }

        // ─── UPDATE DOCTOR PROFILE ────────────────────────────────────────────────

        @Test
        void updateDoctorProfile_shouldUpdateProfile_whenRequestingUserIsAdmin() {
                DoctorProfileRequest doctorRequest = new DoctorProfileRequest();
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(ADMIN_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.ADMIN);
                when(profileService.updateDoctorProfile(DOCTOR_ID, doctorRequest))
                                .thenReturn(new DoctorProfileResponse());

                ResponseEntity<DoctorProfileResponse> response = profileController.updateDoctorProfile(DOCTOR_ID,
                                doctorRequest, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).updateDoctorProfile(DOCTOR_ID, doctorRequest);
        }

        @Test
        void updateDoctorProfile_shouldReturnForbidden_whenRequestingUserIsNotAdminOrSelf() {
                DoctorProfileRequest doctorRequest = new DoctorProfileRequest();
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(PATIENT_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.PATIENT);

                ResponseEntity<DoctorProfileResponse> response = profileController.updateDoctorProfile(DOCTOR_ID,
                                doctorRequest, request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).updateDoctorProfile(any(), any());
        }

        // ─── UPDATE PATIENT PROFILE ───────────────────────────────────────────────

        @Test
        void updatePatientProfile_shouldUpdateProfile_whenRequestingUserIsSelf() {
                PatientProfileRequest patientRequest = new PatientProfileRequest();
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(PATIENT_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.PATIENT);
                when(profileService.updatePatientProfile(PATIENT_ID, patientRequest))
                                .thenReturn(new PatientProfileResponse());

                ResponseEntity<PatientProfileResponse> response = profileController.updatePatientProfile(PATIENT_ID,
                                patientRequest, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(profileService).updatePatientProfile(PATIENT_ID, patientRequest);
        }

        @Test
        void updatePatientProfile_shouldReturnForbidden_whenRequestingUserIsNotAdminOrSelf() {
                PatientProfileRequest patientRequest = new PatientProfileRequest();
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(DOCTOR_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.DOCTOR);

                ResponseEntity<PatientProfileResponse> response = profileController.updatePatientProfile(PATIENT_ID,
                                patientRequest, request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).updatePatientProfile(any(), any());
        }

        // ─── DELETE USER ──────────────────────────────────────────────────────────

        @Test
        void deactivateUser_shouldDeactivate_whenRequestingUserIsAdmin() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(ADMIN_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.ADMIN);

                ResponseEntity<Void> response = profileController.deactivateUser(PATIENT_ID, request);

                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                verify(profileService).deactivateUser(PATIENT_ID, ADMIN_ID);
        }

        @Test
        void deactivateUser_shouldReturnForbidden_whenRequestingUserIsNotAdmin() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(PATIENT_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.PATIENT);

                ResponseEntity<Void> response = profileController.deactivateUser(PATIENT_ID, request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).deactivateUser(any(), any());
        }

        @Test
        void deactivateUser_shouldReturnForbidden_whenAdminTriesToDeleteSelf() {
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID)).thenReturn(ADMIN_ID);
                when(request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE)).thenReturn(Role.ADMIN);

                ResponseEntity<Void> response = profileController.deactivateUser(ADMIN_ID, request);

                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                verify(profileService, never()).deactivateUser(any(), any());
        }
}