package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.enums.PriceChangeRequestStatus;

@Entity
@Table(name = "price_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceChangeRequest extends BaseEntity {

    @Column(name = "old_price", nullable = false)
    private Float oldPrice;

    @Column(name = "new_price", nullable = false)
    private Float newPrice;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private PriceChangeRequestStatus status = PriceChangeRequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", foreignKey = @ForeignKey(name = "fk_price_req_doctor"))
    private Doctor doctor;
}