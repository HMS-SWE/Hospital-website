package com.example.backend.dto;

import lombok.Data;
@Data
public class BookingRequest {
    private Long patientId;
    private Long slotId;
}
