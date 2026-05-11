package com.example.backend.controller;

import com.example.backend.dto.CancellationSummaryResponse;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private MockHttpServletRequest makeRequest(Long userId, Role role) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ID, userId);
        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ROLE, role);
        return request;
    }

    @Test
    void shouldReturn200_whenDoctorCancelsOwnSchedule() {
        Long doctorId = 1L;
        CancellationSummaryResponse summary = CancellationSummaryResponse.builder()
                .cancelledAppointments(3)
                .cancelledTimeSlots(3)
                .operationTimestamp(LocalDateTime.now())
                .build();

        when(scheduleService.cancelDaySchedule(doctorId)).thenReturn(summary);

        ResponseEntity<?> response = scheduleController.cancelDaySchedule(
                doctorId, makeRequest(doctorId, Role.DOCTOR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(summary, response.getBody());
    }

    @Test
    void shouldReturn200_whenAdminCancelsAnyDoctorSchedule() {
        Long doctorId = 1L;
        Long adminId = 99L;
        CancellationSummaryResponse summary = CancellationSummaryResponse.builder()
                .cancelledAppointments(2)
                .cancelledTimeSlots(2)
                .operationTimestamp(LocalDateTime.now())
                .build();

        when(scheduleService.cancelDaySchedule(doctorId)).thenReturn(summary);

        ResponseEntity<?> response = scheduleController.cancelDaySchedule(
                doctorId, makeRequest(adminId, Role.ADMIN));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn403_whenDoctorCancelsAnotherDoctorsSchedule() {
        Long doctorId = 1L;
        Long anotherDoctorId = 2L;

        ResponseEntity<?> response = scheduleController.cancelDaySchedule(
                doctorId, makeRequest(anotherDoctorId, Role.DOCTOR));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(scheduleService, never()).cancelDaySchedule(any());
    }

    @Test
    void shouldReturn403_whenPatientAttemptsToCancel() {
        Long doctorId = 1L;
        Long patientId = 5L;

        ResponseEntity<?> response = scheduleController.cancelDaySchedule(
                doctorId, makeRequest(patientId, Role.PATIENT));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(scheduleService, never()).cancelDaySchedule(any());
    }
}