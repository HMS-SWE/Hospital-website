package com.example.backend.entity;

import com.example.backend.enums.TimeSlotStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots", indexes = {
        @Index(name = "idx_slot_schedule", columnList = "schedule_id"),
        @Index(name = "idx_slot_date_status", columnList = "date, status"),
        @Index(name = "idx_slot_doctor_date", columnList = "schedule_id, date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(name = "fk_slot_schedule"))
    private Schedule schedule;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;
}