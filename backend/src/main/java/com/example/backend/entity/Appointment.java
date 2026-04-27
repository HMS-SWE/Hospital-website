package com.example.backend.entity;

import com.example.backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appt_patient", columnList = "patient_id"),
        @Index(name = "idx_appt_doctor", columnList = "doctor_id"),
        @Index(name = "idx_appt_timeslot", columnList = "time_slot_id"),
        @Index(name = "idx_appt_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appt_patient"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appt_doctor"))
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_slot_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_appt_timeslot"))
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @Column(length = 1000)
    private String notes;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(length = 1000)
    private String feedback;

    @Column(name = "examination_price")
    private Float examinationPrice;
}