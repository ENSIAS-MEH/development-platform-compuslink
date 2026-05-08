package com.campuslink.common.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "saved_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_saved_items_user_type_target",
        columnNames = {"user_id", "target_type", "target_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Decoupled cross-domain reference — plain UUID. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, columnDefinition = "target_type")
    private TargetType targetType;

    /** Polymorphic — no FK constraint by design. */
    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
