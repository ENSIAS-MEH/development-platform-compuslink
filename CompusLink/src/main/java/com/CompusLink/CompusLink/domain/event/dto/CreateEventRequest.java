package com.CompusLink.CompusLink.domain.event.dto;

import com.CompusLink.CompusLink.domain.event.model.EventCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateEventRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotBlank private String location;
    @NotBlank private String city;
    @NotNull @Future private OffsetDateTime eventDate;
    @NotNull private EventCategory category;
    private Integer maxParticipants;
    private String coverUrl;
}
