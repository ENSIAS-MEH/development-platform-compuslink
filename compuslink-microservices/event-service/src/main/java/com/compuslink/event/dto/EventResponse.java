package com.compuslink.event.dto;
import com.compuslink.event.model.EventCategory;
import lombok.Builder; import lombok.Data;
import java.time.OffsetDateTime; import java.util.UUID;
@Data @Builder
public class EventResponse {
    private UUID id; private UUID organizerId; private String organizerName;
    private String title; private String description; private String location; private String city;
    private OffsetDateTime eventDate; private EventCategory category;
    private Integer maxParticipants; private String coverUrl; private boolean cancelled;
    private long participantCount; private OffsetDateTime createdAt;
}
