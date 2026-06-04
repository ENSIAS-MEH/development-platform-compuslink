package com.compuslink.offer.dto;

import com.compuslink.offer.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateOfferRequest {
    @NotNull private OfferType type;
    @NotBlank private String title;
    private String company;
    private String city;
    @NotNull private LocationType locationType;
    private ExperienceLevel experienceLevel;
    private String duration;
    @NotBlank private String description;
    private String domain;
    private LocalDate deadline;
}
