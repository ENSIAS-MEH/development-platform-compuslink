package com.compuslink.event.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @Entity @Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "organizer_id", nullable = false) private UUID organizerId;
    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(nullable = false) private String location;
    @Column(nullable = false) private String city;
    @Column(name = "event_date", nullable = false) private OffsetDateTime eventDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EventCategory category;
    @Column(name = "max_participants") private Integer maxParticipants;
    @Column(name = "cover_url") private String coverUrl;
    @Builder.Default @Column(nullable = false) private boolean cancelled = false;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
}
