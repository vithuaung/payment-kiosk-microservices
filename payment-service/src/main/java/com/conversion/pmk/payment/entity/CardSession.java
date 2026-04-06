package com.conversion.pmk.payment.entity;

import com.conversion.pmk.common.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Card terminal session tied to a payment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_card_session")
public class CardSession {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "card_id", updatable = false, nullable = false)
    private UUID cardId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "card_network", length = 20)
    private String cardNetwork;

    @Column(name = "approval_ref", length = 60)
    private String approvalRef;

    @Column(name = "terminal_ref", length = 60)
    private String terminalRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 20, nullable = false)
    private SessionStatus sessionStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
