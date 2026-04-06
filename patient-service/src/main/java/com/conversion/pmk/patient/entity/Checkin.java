package com.conversion.pmk.patient.entity;

import com.conversion.pmk.common.enums.CheckinType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Transactional record of a patient check-in event
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_checkin")
public class Checkin {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "checkin_id", updatable = false, nullable = false)
    private UUID checkinId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    // Nullable — walk-in patients may not have a scheduled visit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Enumerated(EnumType.STRING)
    @Column(name = "checkin_type", length = 20, nullable = false)
    private CheckinType checkinType;

    @Column(name = "queue_no", length = 20)
    private String queueNo;

    @Column(name = "location_code", length = 20, nullable = false)
    private String locationCode;

    @Column(name = "checkin_at", nullable = false)
    private LocalDateTime checkinAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
