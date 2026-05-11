package com.example.backend.dto.appointment;

import com.example.backend.enums.AppointmentStatus;
import lombok.Data;

@Data
public class VisitStatusUpdateRequest {
    private AppointmentStatus status;
}