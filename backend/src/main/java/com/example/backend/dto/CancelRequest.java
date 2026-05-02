package com.example.backend.dto;

import lombok.Data;

@Data
public class CancelRequest {
    private String reason;
    private Long appointmentId;
}

