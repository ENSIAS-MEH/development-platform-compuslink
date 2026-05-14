package com.CompusLink.CompusLink.domain.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImageResponse {

    private UUID id;

    private String url;

    private Integer sortOrder;

    private Boolean isCover;
}
