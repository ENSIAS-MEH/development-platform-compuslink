package com.compuslink.offer.dto;

import com.compuslink.offer.model.ExperienceLevel;
import com.compuslink.offer.model.LocationType;
import com.compuslink.offer.model.OfferStatus;
import com.compuslink.offer.model.OfferType;
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
public class OfferSummaryResponse {

    private UUID id;
    private UUID posterId;
    private OfferType type;
    private String title;
    private String company;
    private String city;
    private LocationType locationType;
    private ExperienceLevel experienceLevel;
    private String duration;
    private String domain;
    private LocalDate deadline;
    private OfferStatus status;
    private OffsetDateTime createdAt;
}
