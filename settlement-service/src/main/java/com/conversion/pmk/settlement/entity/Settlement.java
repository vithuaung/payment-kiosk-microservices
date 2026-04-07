package com.conversion.pmk.settlement.entity;

import com.conversion.pmk.common.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// One settlement record per payment session
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_settlement")
public class Settlement {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "settlement_id", updatable = false, nullable = false)
    private UUID settlementId;

    // Matches payment_id from payment-service; no cross-service FK
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "session_ref", length = 40, nullable = false)
    private String sessionRef;

    @Column(name = "person_ref", length = 30)
    private String personRef;

    @Column(name = "pay_method", length = 20)
    private String payMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "settle_status", length = 20, nullable = false)
    private SettlementStatus settleStatus;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "max_retry")
    private int maxRetry;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "ext_ref", length = 60)
    private String extRef;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Optimistic locking — prevents double-settle on concurrent sync calls
    @Version
    @Column(name = "version_no")
    private Long version;

    @Builder.Default
    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettleAttempt> attempts = new ArrayList<>();
}
