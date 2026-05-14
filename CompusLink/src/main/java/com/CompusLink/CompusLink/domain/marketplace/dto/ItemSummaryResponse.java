package com.CompusLink.CompusLink.domain.marketplace.dto;

import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
