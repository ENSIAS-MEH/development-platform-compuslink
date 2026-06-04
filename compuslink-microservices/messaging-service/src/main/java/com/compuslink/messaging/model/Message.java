package com.compuslink.messaging.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @Entity @Table(name = "messages")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "conversation_id", nullable = false) private Conversation conversation;
    @Column(name = "sender_id", nullable = false) private UUID senderId;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Column(name = "created_at") private OffsetDateTime createdAt;
    @Builder.Default @Column(name = "is_read") private Boolean isRead = false;
    @PrePersist void prePersist() { this.createdAt = OffsetDateTime.now(); }
}
