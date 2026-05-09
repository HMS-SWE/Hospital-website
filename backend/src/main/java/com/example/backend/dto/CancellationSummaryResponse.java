package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CancellationSummaryResponse {
    private int cancelledAppointments;
    private int cancelledTimeSlots;
    private LocalDateTime operationTimestamp;
}
