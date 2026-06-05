package com.compuslink.marketplace.dto;

import com.compuslink.marketplace.model.ItemCondition;
import com.compuslink.marketplace.model.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ItemSummaryResponse {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String city;
    private ItemCondition condition;
    private String category;
    private ItemStatus status;
    private String coverImageUrl;
    private OffsetDateTime createdAt;
}
