package com.CompusLink.CompusLink.domain.colocation.dto;

import java.util.UUID;

public record ColocImageDTO(
    UUID id,
    String url,
    Integer sortOrder,
    Boolean isCover
) {}