package com.compuslink.marketplace.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemImageResponse {
    private UUID id;
    private String url;
    private Integer sortOrder;
    private Boolean isCover;
}
