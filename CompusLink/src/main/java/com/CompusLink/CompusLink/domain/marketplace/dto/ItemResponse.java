package com.CompusLink.CompusLink.domain.marketplace.dto;

import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
