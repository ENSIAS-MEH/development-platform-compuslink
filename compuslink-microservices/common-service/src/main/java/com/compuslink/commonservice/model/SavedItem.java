package com.compuslink.commonservice.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime; import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @Entity
@Table(name = "saved_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"}))
public class SavedItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Enumerated(EnumType.STRING) @Column(name = "target_type", nullable = false) private TargetType targetType;
    @Column(name = "target_id", nullable = false) private UUID targetId;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
}
