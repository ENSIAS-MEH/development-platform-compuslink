package com.CompusLink.CompusLink.domain.event.dto;

import com.CompusLink.CompusLink.domain.event.model.EventCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private UUID id;
    private UUID organizerId;
    private String organizerName;
    private String title;
    private String description;
    private String location;
    private String city;
    private OffsetDateTime eventDate;
    private EventCategory category;
    private Integer maxParticipants;
    private long participantCount;
    @JsonProperty("isParticipating")
    private boolean isParticipating;
    private String coverUrl;
    private boolean cancelled;
    private OffsetDateTime createdAt;
}
