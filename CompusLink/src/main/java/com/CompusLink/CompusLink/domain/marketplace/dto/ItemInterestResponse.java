package com.CompusLink.CompusLink.domain.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInterestResponse {

    private UUID id;

    private UUID itemId;

    private UUID buyerId;

    private String buyerName;

    private String message;

    private OffsetDateTime createdAt;
}
