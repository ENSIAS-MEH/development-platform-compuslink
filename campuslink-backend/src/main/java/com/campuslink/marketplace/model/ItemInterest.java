package com.campuslink.marketplace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Standalone entity — item_id and user_id are plain UUIDs (no @ManyToOne).
 */
@Entity
@Table(
    name = "item_interests",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_item_interests_item_user",
        columnNames = {"item_id", "user_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
