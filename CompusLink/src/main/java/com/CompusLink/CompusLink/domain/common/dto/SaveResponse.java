package com.CompusLink.CompusLink.domain.common.dto;

import com.CompusLink.CompusLink.domain.common.model.TargetType;
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
public class SaveResponse {

    private UUID id;
    private TargetType targetType;
    private UUID targetId;
    private OffsetDateTime createdAt;

    // Item details (populated for ITEM targetType)
    private String title;
    private BigDecimal price;
    private String city;
    private String category;
    private String condition;
    private String coverImageUrl;
}
