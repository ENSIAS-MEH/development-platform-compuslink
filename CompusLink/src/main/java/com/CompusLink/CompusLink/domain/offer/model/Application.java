package com.CompusLink.CompusLink.domain.offer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(name = "uk_applications_offer_applicant", columnNames = {"offer_id", "applicant_id"}))
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "cv_url_snapshot", nullable = false)
    private String cvUrlSnapshot;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AppStatus status = AppStatus.PENDING;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private OffsetDateTime appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
