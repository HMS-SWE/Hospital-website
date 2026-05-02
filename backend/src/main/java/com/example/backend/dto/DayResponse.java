package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDate;
@Data
public class DayResponse {
    private String dayName;
    private LocalDate date;
    private String startTime;
    private String endTime;

    
}
