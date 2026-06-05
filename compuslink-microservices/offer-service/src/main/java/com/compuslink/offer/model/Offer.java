package com.compuslink.offer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "offers")
public class Offer {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "poster_id", nullable = false) private UUID posterId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OfferType type;
    @Column(nullable = false) private String title;
    private String company;
    private String city;
    @Enumerated(EnumType.STRING) @Column(name = "location_type", nullable = false) private LocationType locationType;
    @Enumerated(EnumType.STRING) @Column(name = "experience_level") private ExperienceLevel experienceLevel;
    private String duration;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    private String domain;
    private LocalDate deadline;
    @Builder.Default @Enumerated(EnumType.STRING) @Column(nullable = false) private OfferStatus status = OfferStatus.OPEN;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
}
