package com.compuslink.marketplace.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ItemInterestResponse {
    private UUID id;
    private UUID itemId;
    private UUID buyerId;
    private String buyerName;
    private String message;
    private OffsetDateTime createdAt;
}
