package com.campuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coloc_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColocPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Decoupled cross-domain reference — plain UUID. */
    @Column(name = "poster_id", nullable = false)
    private UUID posterId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "spots_needed", nullable = false)
    private Integer spotsNeeded;

    @Column(name = "spots_confirmed", nullable = false)
    @Builder.Default
    private Integer spotsConfirmed = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "housing_type", columnDefinition = "housing_type")
    private HousingType housingType;

    @Column(name = "rent_per_person", precision = 10, scale = 2)
    private BigDecimal rentPerPerson;

    @Column(name = "furnished", nullable = false)
    @Builder.Default
    private Boolean furnished = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "coloc_status")
    @Builder.Default
    private ColocStatus status = ColocStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
