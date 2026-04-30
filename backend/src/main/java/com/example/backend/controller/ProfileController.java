package com.example.backend.controller;

import com.example.backend.dto.profile.request.*;
import com.example.backend.dto.profile.response.*;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ─── GET DOCTOR PROFILE ───────────────────────────────────────────────────

    @GetMapping("/doctors/{id}/profile")
    public ResponseEntity<?> getDoctorProfile(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long requestingUserId = extractUserId(request);
        Role requestingRole   = extractRole(request);

        if (!isAdminOrSelf(requestingRole, requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profileService.getDoctorProfile(id));
    }

    // ─── GET PATIENT PROFILE ──────────────────────────────────────────────────

    @GetMapping("/patients/{id}/profile")
    public ResponseEntity<PatientProfileResponse> getPatientProfile(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long requestingUserId = extractUserId(request);
        Role requestingRole   = extractRole(request);

        if (!isAdminOrSelf(requestingRole, requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profileService.getPatientProfile(id));
    }

    // ─── GET USER PROFILE ─────────────────────────────────────────────────────

    @GetMapping("/users/{id}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long requestingUserId = extractUserId(request);
        Role requestingRole   = extractRole(request);

        if (!isAdminOrSelf(requestingRole, requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profileService.getUserProfile(id));
    }

    // ─── UPDATE DOCTOR PROFILE ────────────────────────────────────────────────

    @PutMapping("/doctors/{id}/profile")
    public ResponseEntity<DoctorProfileResponse> updateDoctorProfile(
            @PathVariable Long id,
            @Valid @RequestBody DoctorProfileRequest request,
            HttpServletRequest httpRequest) {

        Long requestingUserId = extractUserId(httpRequest);
        Role requestingRole   = extractRole(httpRequest);

        if (!isAdminOrSelf(requestingRole, requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profileService.updateDoctorProfile(id, request));
    }

    // ─── UPDATE PATIENT PROFILE ───────────────────────────────────────────────

    @PutMapping("/patients/{id}/profile")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @PathVariable Long id,
            @Valid @RequestBody PatientProfileRequest request,
            HttpServletRequest httpRequest) {

        Long requestingUserId = extractUserId(httpRequest);
        Role requestingRole   = extractRole(httpRequest);

        if (!isAdminOrSelf(requestingRole, requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profileService.updatePatientProfile(id, request));
    }

    // ─── DELETE USER ──────────────────────────────────────────────────────────

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long requestingUserId = extractUserId(request);
        Role requestingRole   = extractRole(request);

        if (!isAdmin(requestingRole) || isSelf(requestingUserId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        profileService.deactivateUser(id, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private boolean isAdminOrSelf(Role role, Long requestingUserId, Long targetId) {
        return isAdmin(role) || isSelf(requestingUserId, targetId);
    }

    private boolean isAdmin(Role role) {
        return role == Role.ADMIN;
    }

    private boolean isSelf(Long requestingUserId, Long targetId) {
        return requestingUserId.equals(targetId);
    }

    private Long extractUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID);
    }

    private Role extractRole(HttpServletRequest request) {
        return (Role) request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE);
    }
}