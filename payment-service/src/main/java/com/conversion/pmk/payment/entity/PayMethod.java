package com.conversion.pmk.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Read-only payment method configuration
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cd_pay_method")
public class PayMethod {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "method_id", updatable = false, nullable = false)
    private UUID methodId;

    @Column(name = "method_code", length = 30, unique = true, nullable = false)
    private String methodCode;

    @Column(name = "method_name", length = 60, nullable = false)
    private String methodName;

    @Column(name = "method_group", length = 20, nullable = false)
    private String methodGroup;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
