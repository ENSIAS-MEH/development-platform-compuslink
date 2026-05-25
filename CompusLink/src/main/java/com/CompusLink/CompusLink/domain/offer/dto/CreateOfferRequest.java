package com.CompusLink.CompusLink.domain.offer.dto;

import com.CompusLink.CompusLink.domain.offer.model.ExperienceLevel;
import com.CompusLink.CompusLink.domain.offer.model.LocationType;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateOfferRequest {

    @NotNull
    private OfferType type;

    @NotBlank
    private String title;

    private String company;

    private String city;

    @NotNull
    private LocationType locationType;

    private ExperienceLevel experienceLevel;

    private String duration;

    @NotBlank
    private String description;

    private String domain;

    @FutureOrPresent
    private LocalDate deadline;
}
