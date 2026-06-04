package com.compuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "coloc_interests", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class ColocInterest {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "post_id", nullable = false) private UUID postId;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(columnDefinition = "TEXT") private String message;
    @Builder.Default @Enumerated(EnumType.STRING) @Column(nullable = false) private InterestStatus status = InterestStatus.PENDING;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
}
