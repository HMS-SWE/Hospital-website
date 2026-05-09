package com.example.backend.service;

import com.example.backend.dto.appointment.DoctorAppointmentView;
import com.example.backend.entity.*;
import com.example.backend.enums.*;
import com.example.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @InjectMocks private AppointmentService appointmentService;

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private JwtService jwtService;
    @Mock private CancellationRuleRepository cancellationRuleRepository;

    private static final ZoneId EGYPT_ZONE = ZoneId.of("Africa/Cairo");

    // =========================================================================
    // BOOK APPOINTMENT
    // =========================================================================

    @Test
    @DisplayName("bookAppointment → succeeds when slot is available")
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
    @DisplayName("bookAppointment → throws when slot is already booked")
    void shouldFailWhenSlotNotAvailable() {
        String token = "fake";
        Long userId = 1L;
        Long slotId = 2L;

        TimeSlot slot = new TimeSlot();
        slot.setStatus(TimeSlotStatus.BOOKED);

        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractRole(token)).thenReturn(Role.PATIENT);
        when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));

        assertThrows(RuntimeException.class, () ->
                appointmentService.bookAppointment(token, slotId));
    }

    // =========================================================================
    // CANCEL APPOINTMENT
    // =========================================================================

    @Test
    @DisplayName("cancelAppointment → succeeds within cancellation window")
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

    // =========================================================================
    // EDIT APPOINTMENT
    // =========================================================================

    @Test
    @DisplayName("editAppointment → swaps timeslot successfully")
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

    // =========================================================================
    // GET TODAY'S APPOINTMENTS
    // =========================================================================

    @Test
    @DisplayName("getTodaysAppointments → passes Egypt date and CANCELLED to repo")
    void getTodaysAppointments_passesCorrectParameters() {
        Long doctorId = 1L;
        LocalDate expectedDate = LocalDate.now(EGYPT_ZONE);

        when(appointmentRepository.findTodaysAppointmentsForDoctor(
                eq(doctorId),
                eq(expectedDate),
                eq(AppointmentStatus.CANCELLED)
        )).thenReturn(List.of());

        appointmentService.getTodaysAppointments(doctorId);

        // capture actual arguments passed to the repo
        ArgumentCaptor<LocalDate> dateCaptor =
                ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<AppointmentStatus> statusCaptor =
                ArgumentCaptor.forClass(AppointmentStatus.class);

        verify(appointmentRepository).findTodaysAppointmentsForDoctor(
                eq(doctorId),
                dateCaptor.capture(),
                statusCaptor.capture()
        );

        // must be Egypt's today — not server UTC
        assertThat(dateCaptor.getValue())
                .isEqualTo(LocalDate.now(EGYPT_ZONE));

        // must always exclude CANCELLED — not COMPLETED, not NOSHOW
        assertThat(statusCaptor.getValue())
                .isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("getTodaysAppointments → returns appointments sorted by startTime ASC")
    void getTodaysAppointments_returnsSortedResults() {
        Long doctorId = 1L;

        List<DoctorAppointmentView> mockResults = List.of(
                new DoctorAppointmentView(
                        1L, "Jane Doe",
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        AppointmentStatus.CONFIRMED, null, 200.0f),
                new DoctorAppointmentView(
                        2L, "John Smith",
                        LocalTime.of(9, 30), LocalTime.of(10, 0),
                        AppointmentStatus.CONFIRMED, null, 200.0f),
                new DoctorAppointmentView(
                        3L, "Sara Ali",
                        LocalTime.of(11, 0), LocalTime.of(11, 30),
                        AppointmentStatus.CONFIRMED, "Follow up", 150.0f)
        );

        when(appointmentRepository.findTodaysAppointmentsForDoctor(
                any(), any(), any()
        )).thenReturn(mockResults);

        List<DoctorAppointmentView> result =
                appointmentService.getTodaysAppointments(doctorId);

        assertThat(result).hasSize(3);

        // verify sort order — each startTime is before the next
        assertThat(result.get(0).getStartTime())
                .isBefore(result.get(1).getStartTime());
        assertThat(result.get(1).getStartTime())
                .isBefore(result.get(2).getStartTime());

        // verify patient names came through correctly
        assertThat(result.get(0).getPatientFullName()).isEqualTo("Jane Doe");
        assertThat(result.get(1).getPatientFullName()).isEqualTo("John Smith");
        assertThat(result.get(2).getPatientFullName()).isEqualTo("Sara Ali");
    }

    @Test
    @DisplayName("getTodaysAppointments → returns empty list when no appointments today")
    void getTodaysAppointments_returnsEmpty_whenNone() {
        when(appointmentRepository.findTodaysAppointmentsForDoctor(
                any(), any(), any()
        )).thenReturn(List.of());

        List<DoctorAppointmentView> result =
                appointmentService.getTodaysAppointments(1L);

        assertThat(result).isEmpty();

        // repo still called exactly once even when empty
        verify(appointmentRepository, times(1))
                .findTodaysAppointmentsForDoctor(any(), any(), any());
    }

    @Test
    @DisplayName("getTodaysAppointments → never returns CANCELLED appointments")
    void getTodaysAppointments_neverReturnsCancelled() {
        Long doctorId = 1L;

        // repo returns only non-cancelled — simulating correct filter
        List<DoctorAppointmentView> mockResults = List.of(
                new DoctorAppointmentView(
                        1L, "Jane Doe",
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        AppointmentStatus.CONFIRMED, null, 200.0f),
                new DoctorAppointmentView(
                        2L, "John Smith",
                        LocalTime.of(10, 0), LocalTime.of(10, 30),
                        AppointmentStatus.COMPLETED, null, 200.0f)
        );

        when(appointmentRepository.findTodaysAppointmentsForDoctor(
                any(), any(), any()
        )).thenReturn(mockResults);

        List<DoctorAppointmentView> result =
                appointmentService.getTodaysAppointments(doctorId);

        // none of the results should be CANCELLED
        assertThat(result)
                .extracting(DoctorAppointmentView::getStatus)
                .doesNotContain(AppointmentStatus.CANCELLED);
    }
}