package com.example.backend.controller;

import com.example.backend.dto.BookingRequest;
import com.example.backend.dto.CancelRequest;
import com.example.backend.dto.EditRequest;
import com.example.backend.dto.appointment.AppointmentResponse;
import com.example.backend.dto.appointment.DoctorAppointmentView;
import com.example.backend.dto.appointment.VisitStatusUpdateRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.enums.AppointmentStatus;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @MockitoBean
        private AppointmentService appointmentService;
        @MockitoBean
        private com.example.backend.service.JwtService jwtService;
        @MockitoBean
        private com.example.backend.repository.UserRepository userRepository;
        @MockitoBean
        private com.example.backend.service.MedicalRecordService medicalRecordService;

        private ObjectMapper objectMapper;
        private static final String VALID_TOKEN = "Bearer fake.jwt.token";
        private static final String TOKEN_VALUE = "fake.jwt.token";

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper()
                                .registerModule(new JavaTimeModule());
        }

        // =========================================================================
        // POST /api/appointments/book
        // =========================================================================

        @Test
        @DisplayName("POST /book → 200 when booking succeeds")
        void bookAppointment_returns200_whenSuccessful() throws Exception {
                when(appointmentService.bookAppointment(eq(TOKEN_VALUE), eq(1L)))
                                .thenReturn(new Appointment());

                BookingRequest request = new BookingRequest();
                request.setSlotId(1L);

                mockMvc.perform(post("/api/appointments/book")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(appointmentService).bookAppointment(TOKEN_VALUE, 1L);
        }

        @Test
        @DisplayName("POST /book → 400 when slot not available")
        void bookAppointment_returns400_whenSlotNotAvailable() throws Exception {
                when(appointmentService.bookAppointment(any(), any()))
                                .thenThrow(new RuntimeException("Slot is not available"));

                BookingRequest request = new BookingRequest();
                request.setSlotId(1L);

                mockMvc.perform(post("/api/appointments/book")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Slot is not available"));
        }

        @Test
        @DisplayName("POST /book → 400 when Authorization header missing")
        void bookAppointment_returns400_whenNoAuthHeader() throws Exception {
                BookingRequest request = new BookingRequest();
                request.setSlotId(1L);

                mockMvc.perform(post("/api/appointments/book")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        // =========================================================================
        // POST /api/appointments/cancel
        // =========================================================================

        @Test
        @DisplayName("POST /cancel → 200 when cancellation succeeds")
        void cancelAppointment_returns200_whenSuccessful() throws Exception {
                doNothing().when(appointmentService)
                                .cancelAppointment(eq(TOKEN_VALUE), eq(1L), eq("Personal reason"));

                CancelRequest request = new CancelRequest();
                request.setAppointmentId(1L);
                request.setReason("Personal reason");

                mockMvc.perform(post("/api/appointments/cancel")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(appointmentService).cancelAppointment(TOKEN_VALUE, 1L, "Personal reason");
        }

        @Test
        @DisplayName("POST /cancel → 400 when appointment not found")
        void cancelAppointment_returns400_whenNotFound() throws Exception {
                doThrow(new RuntimeException("Appointment not found"))
                                .when(appointmentService).cancelAppointment(any(), anyLong(), any());

                CancelRequest request = new CancelRequest();
                request.setAppointmentId(99L);
                request.setReason("reason");

                mockMvc.perform(post("/api/appointments/cancel")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Appointment not found"));
        }

        @Test
        @DisplayName("POST /cancel → 400 when cancelling too late")
        void cancelAppointment_returns400_whenTooLate() throws Exception {
                doThrow(new RuntimeException("Cancellation window has passed"))
                                .when(appointmentService).cancelAppointment(any(), anyLong(), any());

                CancelRequest request = new CancelRequest();
                request.setAppointmentId(1L);
                request.setReason("reason");

                mockMvc.perform(post("/api/appointments/cancel")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Cancellation window has passed"));
        }

        @Test
        @DisplayName("POST /cancel → 400 when not the appointment owner")
        void cancelAppointment_returns400_whenNotOwner() throws Exception {
                doThrow(new RuntimeException("You can only cancel your own appointments"))
                                .when(appointmentService).cancelAppointment(any(), anyLong(), any());

                CancelRequest request = new CancelRequest();
                request.setAppointmentId(1L);
                request.setReason("reason");

                mockMvc.perform(post("/api/appointments/cancel")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("You can only cancel your own appointments"));
        }

        // =========================================================================
        // POST /api/appointments/edit
        // =========================================================================

        @Test
        @DisplayName("POST /edit → 200 when rescheduling succeeds")
        void editAppointment_returns200_whenSuccessful() throws Exception {
                doNothing().when(appointmentService)
                                .editAppointment(eq(TOKEN_VALUE), eq(1L), eq(2L));

                EditRequest request = new EditRequest();
                request.setAppointmentId(1L);
                request.setNewSlotId(2L);

                mockMvc.perform(post("/api/appointments/edit")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(appointmentService).editAppointment(TOKEN_VALUE, 1L, 2L);
        }

        @Test
        @DisplayName("POST /edit → 400 when new slot not available")
        void editAppointment_returns400_whenNewSlotNotAvailable() throws Exception {
                doThrow(new RuntimeException("New slot is not available"))
                                .when(appointmentService).editAppointment(any(), anyLong(), anyLong());

                EditRequest request = new EditRequest();
                request.setAppointmentId(1L);
                request.setNewSlotId(99L);

                mockMvc.perform(post("/api/appointments/edit")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("New slot is not available"));
        }

        @Test
        @DisplayName("POST /edit → 400 when appointment not found")
        void editAppointment_returns400_whenAppointmentNotFound() throws Exception {
                doThrow(new RuntimeException("Appointment not found"))
                                .when(appointmentService).editAppointment(any(), anyLong(), anyLong());

                EditRequest request = new EditRequest();
                request.setAppointmentId(99L);
                request.setNewSlotId(2L);

                mockMvc.perform(post("/api/appointments/edit")
                                .header("Authorization", VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Appointment not found"));
        }

        // =========================================================================
        // GET /api/appointments/myAppointments
        // =========================================================================

        @Test
        @DisplayName("GET /myAppointments → 200 with list")
        void getMyAppointments_returns200_withList() throws Exception {

                AppointmentResponse response = new AppointmentResponse();

                when(appointmentService.getMyAppointments(TOKEN_VALUE))
                                .thenReturn(List.of(response));

                mockMvc.perform(get("/api/appointments/myAppointments")
                                .header("Authorization", VALID_TOKEN))
                                .andExpect(status().isOk());

                verify(appointmentService).getMyAppointments(TOKEN_VALUE);
        }

        @Test
        @DisplayName("GET /myAppointments → 200 with empty list when no appointments")
        void getMyAppointments_returns200_withEmptyList() throws Exception {
                when(appointmentService.getMyAppointments(TOKEN_VALUE))
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/appointments/myAppointments")
                                .header("Authorization", VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /myAppointments → 400 when token invalid")
        void getMyAppointments_returns400_whenTokenInvalid() throws Exception {
                when(appointmentService.getMyAppointments(any()))
                                .thenThrow(new RuntimeException("Invalid token"));

                mockMvc.perform(get("/api/appointments/myAppointments")
                                .header("Authorization", VALID_TOKEN))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Invalid token"));
        }

        // =========================================================================
        // GET /api/appointments/doctor/today
        // =========================================================================

        @Test
        @DisplayName("GET /doctor/today → 200 with today's appointments")
        void getDoctorTodaySchedule_returns200_withAppointments() throws Exception {
                List<DoctorAppointmentView> mockResults = List.of(
                                new DoctorAppointmentView(
                                                1L, "Jane Doe",
                                                LocalTime.of(9, 0), LocalTime.of(9, 30),
                                                AppointmentStatus.CONFIRMED, null, 200.0f),
                                new DoctorAppointmentView(
                                                2L, "John Smith",
                                                LocalTime.of(9, 30), LocalTime.of(10, 0),
                                                AppointmentStatus.CONFIRMED, null, 150.0f));

                when(appointmentService.getTodaysAppointmentsForDoctor(4L))
                                .thenReturn(mockResults);

                mockMvc.perform(get("/api/appointments/doctor/today")
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, 4L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].patientFullName").value("Jane Doe"))
                                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                                .andExpect(jsonPath("$[1].patientFullName").value("John Smith"))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2));

                verify(appointmentService).getTodaysAppointmentsForDoctor(4L);
        }

        @Test
        @DisplayName("GET /doctor/today → 200 with empty list when no appointments today")
        void getDoctorTodaySchedule_returns200_whenNoAppointments() throws Exception {
                when(appointmentService.getTodaysAppointmentsForDoctor(any()))
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/appointments/doctor/today")
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, 4L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("GET /doctor/today → results sorted by startTime ASC")
        void getDoctorTodaySchedule_returnsSortedByStartTime() throws Exception {
                List<DoctorAppointmentView> mockResults = List.of(
                                new DoctorAppointmentView(
                                                1L, "Jane Doe",
                                                LocalTime.of(9, 0), LocalTime.of(9, 30),
                                                AppointmentStatus.CONFIRMED, null, 200.0f),
                                new DoctorAppointmentView(
                                                2L, "John Smith",
                                                LocalTime.of(10, 0), LocalTime.of(10, 30),
                                                AppointmentStatus.CONFIRMED, null, 150.0f));

                when(appointmentService.getTodaysAppointmentsForDoctor(any()))
                                .thenReturn(mockResults);

                mockMvc.perform(get("/api/appointments/doctor/today")
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, 4L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                                .andExpect(jsonPath("$[1].startTime").value("10:00:00"));
        }

        @Test
        @DisplayName("GET /doctor/today → never contains CANCELLED appointments")
        void getDoctorTodaySchedule_neverReturnsCancelled() throws Exception {
                List<DoctorAppointmentView> mockResults = List.of(
                                new DoctorAppointmentView(
                                                1L, "Jane Doe",
                                                LocalTime.of(9, 0), LocalTime.of(9, 30),
                                                AppointmentStatus.CONFIRMED, null, 200.0f));

                when(appointmentService.getTodaysAppointmentsForDoctor(any()))
                                .thenReturn(mockResults);

                mockMvc.perform(get("/api/appointments/doctor/today")
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, 4L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
        }
        // ========================= UPDATE VISIT STATUS =========================

        @Test
        void updateVisitStatus_success() throws Exception {
                doNothing().when(appointmentService)
                                .updateVisitStatus(anyLong(), any(), anyLong());

                mockMvc.perform(patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"COMPLETED\"}")
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ID, 1L)
                                .requestAttr(AuthenticatedUserRequestAttributes.USER_ROLE, Role.DOCTOR))
                                .andExpect(status().isOk());

                verify(appointmentService)
                                .updateVisitStatus(eq(1L), eq(AppointmentStatus.COMPLETED), eq(1L));
        }

        @Test
        void updateVisitStatus_unauthorized() throws Exception {
                mockMvc.perform(patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"COMPLETED\"}"))
                                .andExpect(status().isUnauthorized());
        }
}