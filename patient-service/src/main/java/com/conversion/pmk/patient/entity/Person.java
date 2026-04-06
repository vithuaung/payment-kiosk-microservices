package com.conversion.pmk.patient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Persisted patient identity record
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "md_person")
public class Person {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "person_id", updatable = false, nullable = false)
    private UUID personId;

    @Column(name = "id_ref", length = 20, unique = true, nullable = false)
    private String idRef;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "mobile_no", length = 20)
    private String mobileNo;

    @Column(name = "email_addr", length = 100)
    private String emailAddr;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
