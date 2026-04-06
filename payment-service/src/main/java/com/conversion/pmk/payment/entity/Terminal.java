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

// Read-only kiosk terminal configuration
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cd_terminal")
public class Terminal {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "terminal_id", updatable = false, nullable = false)
    private UUID terminalId;

    @Column(name = "terminal_code", length = 20, unique = true, nullable = false)
    private String terminalCode;

    @Column(name = "location_code", length = 20)
    private String locationCode;

    @Column(name = "terminal_status", length = 20)
    private String terminalStatus;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
