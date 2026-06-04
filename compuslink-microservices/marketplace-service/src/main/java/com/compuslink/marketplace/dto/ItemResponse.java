package com.compuslink.marketplace.dto;

import com.compuslink.marketplace.model.ItemCondition;
import com.compuslink.marketplace.model.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ItemResponse {
    private UUID id;
    private UUID sellerId;
    private String sellerName;
    private String title;
    private String description;
    private BigDecimal price;
    private String city;
    private ItemCondition condition;
    private String category;
    private ItemStatus status;
    private List<ItemImageResponse> images;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
