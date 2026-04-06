package com.conversion.pmk.payment.entity;

import com.conversion.pmk.common.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// Cash machine session tied to a payment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_cash_session")
public class CashSession {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "cash_id", updatable = false, nullable = false)
    private UUID cashId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "inserted_amt", precision = 10, scale = 2)
    private BigDecimal insertedAmt;

    @Column(name = "returned_amt", precision = 10, scale = 2)
    private BigDecimal returnedAmt;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 20, nullable = false)
    private SessionStatus sessionStatus;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
