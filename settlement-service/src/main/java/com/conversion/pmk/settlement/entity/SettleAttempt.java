package com.conversion.pmk.settlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// One row per SAP call attempt for a settlement
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_settle_attempt")
public class SettleAttempt {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "attempt_id", updatable = false, nullable = false)
    private UUID attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @Column(name = "attempt_no", nullable = false)
    private int attemptNo;

    // OK, FAILED, or TIMEOUT
    @Column(name = "result_status", length = 20, nullable = false)
    private String resultStatus;

    @Column(name = "sent_data", columnDefinition = "NVARCHAR(MAX)")
    private String sentData;

    @Column(name = "recv_data", columnDefinition = "NVARCHAR(MAX)")
    private String recvData;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
