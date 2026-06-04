package com.compuslink.offer.dto;

import com.compuslink.offer.model.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class OfferResponse {
    private UUID id;
    private UUID posterId;
    private String posterName;
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
}
