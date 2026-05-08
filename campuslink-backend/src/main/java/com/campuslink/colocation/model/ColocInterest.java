package com.campuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Standalone entity — post_id and user_id are plain UUIDs (no @ManyToOne).
 */
@Entity
@Table(
    name = "coloc_interests",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_coloc_interests_post_user",
        columnNames = {"post_id", "user_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColocInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "interest_status")
    @Builder.Default
    private InterestStatus status = InterestStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
