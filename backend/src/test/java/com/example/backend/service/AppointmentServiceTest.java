package com.example.backend.service;
import com.example.backend.entity.*;
import com.example.backend.enums.*;
import com.example.backend.repository.*;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; 
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private JwtService jwtService;
    @Mock private CancellationRuleRepository cancellationRuleRepository;

    @Test
    void shouldBookAppointmentSuccessfully() {

        String token = "fake";
        Long userId = 1L;
        Long slotId = 2L;

        Patient patient = new Patient();
        patient.setId(userId);
        patient.setAppointments(new ArrayList<>());

        Doctor doctor = new Doctor();

        TimeSlot slot = new TimeSlot();
        slot.setId(slotId);
        slot.setStatus(TimeSlotStatus.AVAILABLE);
        slot.setDate(LocalDate.now().plusDays(1));
        slot.setStartTime(LocalTime.now().plusHours(2));

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        slot.setSchedule(schedule);

        when(jwtService.extractUserId(token)).thenReturn(userId);
        lenient().when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        lenient().when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.extractRole(token)).thenReturn(Role.PATIENT);
        Appointment result = appointmentService.bookAppointment(token, slotId);

        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
    }

    @Test
void shouldFailWhenSlotNotAvailable() {
    String token = "fake";
    Long userId = 1L;
    Long slotId = 2L;

    TimeSlot slot = new TimeSlot();
    slot.setStatus(TimeSlotStatus.BOOKED); 

    when(jwtService.extractUserId(token)).thenReturn(userId);
    when(jwtService.extractRole(token)).thenReturn(Role.PATIENT);
    when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));


    assertThrows(RuntimeException.class, () -> {
        appointmentService.bookAppointment(token, slotId);
    });
}
    @Test
void shouldCancelAppointmentSuccessfully() {

    String token = "fake";
    Long userId = 1L;

    Patient patient = new Patient();
    patient.setId(userId);

    TimeSlot slot = new TimeSlot();
    slot.setDate(LocalDate.now().plusDays(1));
    slot.setStartTime(LocalTime.now().plusHours(3));

    Appointment appointment = new Appointment();
    appointment.setId(1L);
    appointment.setPatient(patient);
    appointment.setTimeSlot(slot);
    appointment.setStatus(AppointmentStatus.CONFIRMED);

    CancellationRule rule = new CancellationRule();
    rule.setMinimumNoticeHours(1);

    when(jwtService.extractUserId(token)).thenReturn(userId);
    when(jwtService.extractRole(token)).thenReturn(Role.PATIENT);
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
    when(cancellationRuleRepository.findAll()).thenReturn(List.of(rule));

    appointmentService.cancelAppointment(token, 1L, "test");

    assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
}
@Test
void shouldEditAppointmentSuccessfully() {

    String token = "fake";
    Long userId = 1L;

    Patient patient = new Patient();
    patient.setId(userId);
    patient.setAppointments(new ArrayList<>());

    TimeSlot oldSlot = new TimeSlot();
    oldSlot.setId(1L);

    TimeSlot newSlot = new TimeSlot();
    newSlot.setId(2L);
    newSlot.setStatus(TimeSlotStatus.AVAILABLE);
    newSlot.setDate(LocalDate.now().plusDays(1));
    newSlot.setStartTime(LocalTime.now().plusHours(2));

    Appointment appointment = new Appointment();
    appointment.setId(1L);
    appointment.setPatient(patient);
    appointment.setTimeSlot(oldSlot);
    appointment.setStatus(AppointmentStatus.CONFIRMED);

    when(jwtService.extractUserId(token)).thenReturn(userId);
    when(jwtService.extractRole(token)).thenReturn(Role.PATIENT);
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
    when(timeSlotRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(newSlot));

    appointmentService.editAppointment(token, 1L, 2L);

    assertEquals(newSlot, appointment.getTimeSlot());
}
}