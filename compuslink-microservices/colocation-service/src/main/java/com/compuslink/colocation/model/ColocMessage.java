package com.compuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "coloc_messages")
public class ColocMessage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "interest_id", nullable = false) private UUID interestId;
    @Column(name = "sender_id", nullable = false) private UUID senderId;
    @Column(columnDefinition = "TEXT", nullable = false) private String content;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
}
