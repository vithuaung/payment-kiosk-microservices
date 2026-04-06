package com.conversion.pmk.payment.entity;

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

// Individual bill line item linked to a payment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_bill_item")
public class BillItem {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "bill_item_id", updatable = false, nullable = false)
    private UUID billItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "bill_ref", length = 40, nullable = false)
    private String billRef;

    @Column(name = "bill_seq", nullable = false)
    private int billSeq;

    @Column(name = "billed_amt", nullable = false, precision = 10, scale = 2)
    private BigDecimal billedAmt;

    @Column(name = "payable_amt", nullable = false, precision = 10, scale = 2)
    private BigDecimal payableAmt;

    @Column(name = "org_code", length = 20)
    private String orgCode;

    @Column(name = "case_ref", length = 40)
    private String caseRef;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
