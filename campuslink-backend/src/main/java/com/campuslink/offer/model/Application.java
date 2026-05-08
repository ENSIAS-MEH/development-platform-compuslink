package com.campuslink.offer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "applications",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_applications_offer_applicant",
        columnNames = {"offer_id", "applicant_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Decoupled cross-domain reference — plain UUID. */
    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    /** Decoupled cross-domain reference — plain UUID. */
    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "cv_url_snapshot", length = 500)
    private String cvUrlSnapshot;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "app_status")
    @Builder.Default
    private AppStatus status = AppStatus.PENDING;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private OffsetDateTime appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
