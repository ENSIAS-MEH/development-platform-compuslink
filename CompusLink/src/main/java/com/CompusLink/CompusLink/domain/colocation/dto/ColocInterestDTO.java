package com.CompusLink.CompusLink.domain.colocation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;

public record ColocInterestDTO(
    UUID id,
    UUID postId,
    UUID userId,
    String message,
    InterestStatus status,
    OffsetDateTime createdAt
) {}