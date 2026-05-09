package com.example.backend.dto;

import lombok.Data;

@Data
public class AppointmentResponse {
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private Float examinationPrice;

}
