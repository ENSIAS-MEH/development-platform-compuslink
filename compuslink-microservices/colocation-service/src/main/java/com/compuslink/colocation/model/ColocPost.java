package com.compuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "coloc_posts")
public class ColocPost {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "poster_id", nullable = false)
    private UUID posterId;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String address;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "spots_needed", nullable = false) private Integer spotsNeeded;
    @Builder.Default @Column(name = "spots_confirmed", nullable = false) private Integer spotsConfirmed = 0;
    @Enumerated(EnumType.STRING) @Column(name = "housing_type", nullable = false) private HousingType housingType;
    @Column(name = "rent_per_person", precision = 10, scale = 2, nullable = false) private BigDecimal rentPerPerson;
    @Builder.Default @Column(nullable = false) private Boolean furnished = false;
    @Builder.Default @Enumerated(EnumType.STRING) @Column(nullable = false) private ColocStatus status = ColocStatus.OPEN;
    @Column(name = "cover_url", length = 500) private String coverUrl;
    @Builder.Default @Column(name = "is_blocked", nullable = false) private Boolean isBlocked = false;
    @Column(name = "blocked_at") private OffsetDateTime blockedAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
}
