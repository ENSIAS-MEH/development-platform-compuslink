package com.compuslink.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class CvResponse {
    private UUID id;
    private String fileUrl;
    private String originalName;
    private OffsetDateTime uploadedAt;
    @JsonProperty("default")
    private boolean isDefault;
}
