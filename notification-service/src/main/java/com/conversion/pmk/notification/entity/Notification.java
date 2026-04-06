package com.conversion.pmk.notification.entity;

import com.conversion.pmk.common.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Persists every notification attempt regardless of outcome
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mtxn_notification")
public class Notification {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "notif_id", updatable = false, nullable = false)
    private UUID notifId;

    // References the payment this notification belongs to (no cross-service FK)
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notif_channel", length = 10, nullable = false)
    private NotificationChannel notifChannel;

    @Column(name = "recipient", length = 100, nullable = false)
    private String recipient;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "body_text", columnDefinition = "NVARCHAR(MAX)")
    private String bodyText;

    // PENDING, SENT, or FAILED
    @Column(name = "send_status", length = 20, nullable = false)
    private String sendStatus;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
