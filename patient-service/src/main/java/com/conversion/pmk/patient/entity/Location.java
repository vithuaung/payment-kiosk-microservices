package com.conversion.pmk.patient.entity;

import com.conversion.pmk.common.enums.LocationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// Reference data for kiosk and counter locations
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cd_location")
public class Location {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "location_id", updatable = false, nullable = false)
    private UUID locationId;

    @Column(name = "location_code", length = 20, unique = true, nullable = false)
    private String locationCode;

    @Column(name = "location_name", length = 100, nullable = false)
    private String locationName;

    @Column(name = "parent_code", length = 20)
    private String parentCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 20, nullable = false)
    private LocationType locationType;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
