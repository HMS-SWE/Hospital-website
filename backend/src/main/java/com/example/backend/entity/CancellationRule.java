package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cancellation_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRule extends BaseEntity {

    @Column(name = "minimum_notice_hours", nullable = false)
    private Integer minimumNoticeHours;
}