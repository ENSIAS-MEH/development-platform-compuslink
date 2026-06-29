package com.compuslink.colocation.dto;

import com.compuslink.colocation.model.AmenityType;
import java.util.UUID;

public record ColocAmenityDTO(UUID id, AmenityType amenityType) {}
