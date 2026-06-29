package com.CompusLink.CompusLink.domain.messaging.model;

import com.CompusLink.CompusLink.domain.user.model.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private Users user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private Users user2;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_message_at")
    private OffsetDateTime lastMessageAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
