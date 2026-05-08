package com.campuslink.offer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Decoupled cross-domain reference — plain UUID. */
    @Column(name = "poster_id", nullable = false)
    private UUID posterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "offer_type")
    private OfferType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "company", length = 255)
    private String company;

    @Column(name = "city", length = 255)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", columnDefinition = "location_type")
    private LocationType locationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", columnDefinition = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "offer_status")
    @Builder.Default
    private OfferStatus status = OfferStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
