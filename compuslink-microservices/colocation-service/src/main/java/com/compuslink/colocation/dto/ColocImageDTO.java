package com.compuslink.colocation.dto;

import java.util.UUID;

public record ColocImageDTO(UUID id, String url, Integer sortOrder, Boolean isCover) {}
