package com.CompusLink.CompusLink.domain.colocation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import com.CompusLink.CompusLink.domain.colocation.model.HousingType;

import lombok.Builder;

@Builder
public record ColocPostDTO(
    UUID id,
    UUID posterId,
    String title,
    String description,
    String city,
    String address,
    LocalDate startDate,
    Integer spotsNeeded,
    Integer spotsConfirmed,
    HousingType housingType,
    BigDecimal rentPerPerson,
    Boolean furnished,
    ColocStatus status,
    String coverUrl, // Ajouté pour le résumé des listes
    Long totalInterests, // Requis par Task 5.1 (nombre total d'intéressés)
    Long pendingInterests, // Requis par Task 5.1 (visibilité conditionnelle)
    List<ColocAmenityDTO> amenities,
    List<ColocImageDTO> images,
    OffsetDateTime createdAt
) {}