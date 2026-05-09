package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.backend.dto.*;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.ScheduleService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/doctor/{doctorId}/available-days")
    public ResponseEntity<?> getAvailableDays(@PathVariable Long doctorId) {
        try {
            List<DayResponse> availableDays = scheduleService.getAvailableDaysForDoctor(doctorId);
            return ResponseEntity.ok(availableDays);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}/available-slots")
    public ResponseEntity<?> getAvailableSlots(@PathVariable Long doctorId, @RequestParam String date) {
        try {
            List<SlotResponse> availableSlots = scheduleService.getAvailableSlotsForDoctorAndDate(doctorId,
                    java.time.LocalDate.parse(date));
            return ResponseEntity.ok(availableSlots);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/doctor/{doctorId}/schedule/cancel")
    public ResponseEntity<?> cancelDaySchedule(@PathVariable Long doctorId,
            HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ID);
        Role authenticatedRole = (Role) request.getAttribute(
                AuthenticatedUserRequestAttributes.USER_ROLE);

        if (authenticatedRole != Role.ADMIN && !authenticatedUserId.equals(doctorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Forbidden\"}");
        }

        CancellationSummaryResponse response = scheduleService.cancelDaySchedule(doctorId);
        return ResponseEntity.ok(response);
    }
}
