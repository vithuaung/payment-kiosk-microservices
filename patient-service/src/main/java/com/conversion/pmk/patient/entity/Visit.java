package com.conversion.pmk.patient.entity;

import com.conversion.pmk.common.enums.VisitStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Scheduled or completed visit linked to a patient
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "md_visit")
public class Visit {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "visit_id", updatable = false, nullable = false)
    private UUID visitId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "counter_code", length = 20, nullable = false)
    private String counterCode;

    @Column(name = "counter_name", length = 100)
    private String counterName;

    @Column(name = "queue_no", length = 20)
    private String queueNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", length = 20, nullable = false)
    private VisitStatus visitStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
