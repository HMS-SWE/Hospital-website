package com.example.backend.controller;

import com.example.backend.dto.BookingRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.AppointmentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.CancelRequest;
import com.example.backend.dto.EditRequest;
import com.example.backend.dto.appointment.VisitStatusUpdateRequest;
import com.example.backend.dto.appointment.DoctorAppointmentView;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Appointment appointment = appointmentService.bookAppointment(token, request.getSlotId());

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelAppointment(@RequestBody CancelRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {

            long id = request.getAppointmentId();
            String token = authHeader.substring(7);

            appointmentService.cancelAppointment(token, id, request.getReason());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editAppointment(@RequestBody EditRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            long appointmentId = request.getAppointmentId();
            long newSlotId = request.getNewSlotId();
            String token = authHeader.substring(7);

            appointmentService.editAppointment(token, appointmentId, newSlotId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/myAppointments")
    public ResponseEntity<?> getMyAppointments(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            return ResponseEntity.ok(appointmentService.getMyAppointments(token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateVisitStatus(@PathVariable Long id,
            @RequestBody VisitStatusUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        Role authenticatedRole = (Role) httpRequest.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ROLE);

        if (authenticatedUserId == null || authenticatedRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\"Unauthorized\"}");
        }

        if (authenticatedRole == Role.PATIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Forbidden\"}");
        }

        try {
            appointmentService.updateVisitStatus(id, request.getStatus(), authenticatedUserId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Forbidden")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"message\":\"Forbidden\"}");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // getting doctor's appointments for today
    @GetMapping("/doctor/today")
    public ResponseEntity<List<DoctorAppointmentView>> getDoctorTodaySchedule(
            HttpServletRequest request) {

        Long doctorId = (Long) request.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);

        return ResponseEntity.ok(
                appointmentService.getTodaysAppointmentsForDoctor(doctorId));
    }
}