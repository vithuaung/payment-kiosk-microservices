package com.conversion.pmk.payment.entity;

import com.conversion.pmk.common.enums.PaymentMethod;
import com.conversion.pmk.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Main payment transaction record
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_payment")
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @Column(name = "session_ref", length = 40, unique = true, nullable = false)
    private String sessionRef;

    @Column(name = "terminal_code", length = 20, nullable = false)
    private String terminalCode;

    @Column(name = "person_ref", length = 20, nullable = false)
    private String personRef;

    @Column(name = "total_amt", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmt;

    @Column(name = "paid_amt", precision = 10, scale = 2)
    private BigDecimal paidAmt;

    @Column(name = "change_amt", precision = 10, scale = 2)
    private BigDecimal changeAmt;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_method", length = 30, nullable = false)
    private PaymentMethod payMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", length = 20, nullable = false)
    private PaymentStatus payStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Bill items belonging to this payment
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> billItems;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private CashSession cashSession;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private CardSession cardSession;
}
