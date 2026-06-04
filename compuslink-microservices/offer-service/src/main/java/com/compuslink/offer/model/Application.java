package com.compuslink.offer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"offer_id", "applicant_id"}))
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "offer_id", nullable = false) private UUID offerId;
    @Column(name = "applicant_id", nullable = false) private UUID applicantId;
    @Column(name = "cv_url_snapshot", nullable = false) private String cvUrlSnapshot;
    @Column(columnDefinition = "TEXT") private String message;
    @Builder.Default @Enumerated(EnumType.STRING) @Column(nullable = false) private AppStatus status = AppStatus.PENDING;
    @CreationTimestamp @Column(name = "applied_at", nullable = false, updatable = false) private OffsetDateTime appliedAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
}
