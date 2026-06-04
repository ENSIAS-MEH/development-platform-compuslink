package com.compuslink.messaging.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @Entity @Table(name = "conversations")
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "user1_id", nullable = false) private UUID user1Id;
    @Column(name = "user2_id", nullable = false) private UUID user2Id;
    @Column(name = "created_at") private OffsetDateTime createdAt;
    @Column(name = "last_message_at") private OffsetDateTime lastMessageAt;
    @PrePersist void prePersist() { this.createdAt = OffsetDateTime.now(); }
}
