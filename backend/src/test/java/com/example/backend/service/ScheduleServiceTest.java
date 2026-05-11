package com.example.backend.service;

import com.example.backend.dto.CancellationSummaryResponse;
import com.example.backend.entity.*;
import com.example.backend.enums.AppointmentStatus;
import com.example.backend.enums.TimeSlotStatus;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.ScheduleRepository;
import com.example.backend.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private ScheduleRepository scheduleRepository;

    // ── Helper ────────────────────────────────────────────────────────────────

    private Appointment makeAppointment(Long id, Long slotId) {
        TimeSlot slot = new TimeSlot();
        slot.setId(slotId);
        slot.setStatus(TimeSlotStatus.BOOKED);
        slot.setDate(LocalDate.now());

        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setTimeSlot(slot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointment;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void shouldReturnZeroCounts_whenNoCancellableAppointmentsExist() {
        when(appointmentRepository.findCancellableAppointments(
                any(), any(), any()))
                .thenReturn(Collections.emptyList());

        CancellationSummaryResponse response = scheduleService.cancelDaySchedule(1L);

        assertEquals(0, response.getCancelledAppointments());
        assertEquals(0, response.getCancelledTimeSlots());
        assertNotNull(response.getOperationTimestamp());

        verify(appointmentRepository, never()).bulkUpdateStatus(any(), any());
        verify(timeSlotRepository, never()).bulkUpdateStatus(any(), any());
    }

    @Test
    void shouldCancelOnlyConfirmedAppointments_whenSomAreAlreadyCompleted() {
        // Only CONFIRMED appointments are returned by the query (COMPLETED are filtered out)
        List<Appointment> cancellable = List.of(
                makeAppointment(1L, 10L),
                makeAppointment(2L, 20L)
        );

        when(appointmentRepository.findCancellableAppointments(
                eq(1L), eq(LocalDate.now()), any()))
                .thenReturn(cancellable);
        when(appointmentRepository.bulkUpdateStatus(any(), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(2);
        when(timeSlotRepository.bulkUpdateStatus(any(), eq(TimeSlotStatus.BLOCKED)))
                .thenReturn(2);

        CancellationSummaryResponse response = scheduleService.cancelDaySchedule(1L);

        assertEquals(2, response.getCancelledAppointments());
        assertEquals(2, response.getCancelledTimeSlots());
    }

    @Test
    void shouldBulkCancelAllConfirmedAppointments_successfully() {
        List<Appointment> cancellable = List.of(
                makeAppointment(1L, 10L),
                makeAppointment(2L, 20L),
                makeAppointment(3L, 30L)
        );

        when(appointmentRepository.findCancellableAppointments(
                eq(1L), eq(LocalDate.now()), any()))
                .thenReturn(cancellable);
        when(appointmentRepository.bulkUpdateStatus(
                eq(List.of(1L, 2L, 3L)), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(3);
        when(timeSlotRepository.bulkUpdateStatus(
                eq(List.of(10L, 20L, 30L)), eq(TimeSlotStatus.BLOCKED)))
                .thenReturn(3);

        CancellationSummaryResponse response = scheduleService.cancelDaySchedule(1L);

        assertEquals(3, response.getCancelledAppointments());
        assertEquals(3, response.getCancelledTimeSlots());
        assertNotNull(response.getOperationTimestamp());
    }

    @Test
    void shouldSyncSlotStatus_whenAppointmentsAreCancelled() {
        Appointment appointment = makeAppointment(1L, 10L);

        when(appointmentRepository.findCancellableAppointments(
                eq(1L), eq(LocalDate.now()), any()))
                .thenReturn(List.of(appointment));
        when(appointmentRepository.bulkUpdateStatus(any(), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(1);
        when(timeSlotRepository.bulkUpdateStatus(any(), eq(TimeSlotStatus.BLOCKED)))
                .thenReturn(1);

        scheduleService.cancelDaySchedule(1L);

        verify(timeSlotRepository).bulkUpdateStatus(
                eq(List.of(10L)), eq(TimeSlotStatus.BLOCKED));
    }
}