package com.CompusLink.CompusLink.domain.offer.dto;

import com.CompusLink.CompusLink.domain.offer.model.ExperienceLevel;
import com.CompusLink.CompusLink.domain.offer.model.LocationType;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {

    private UUID id;
    private UUID posterId;
    private OfferType type;
    private String title;
    private String company;
    private String city;
    private LocationType locationType;
    private ExperienceLevel experienceLevel;
    private String duration;
    private String description;
    private String domain;
    private LocalDate deadline;
    private OfferStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // -1 if the viewer is not the poster
    private long applicationCount;
}
