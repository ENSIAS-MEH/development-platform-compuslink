package com.CompusLink.CompusLink.domain.colocation.dto;

import java.util.UUID;

import com.CompusLink.CompusLink.domain.colocation.model.AmenityType;

public record ColocAmenityDTO(
    UUID id,
    AmenityType amenityType
) {}