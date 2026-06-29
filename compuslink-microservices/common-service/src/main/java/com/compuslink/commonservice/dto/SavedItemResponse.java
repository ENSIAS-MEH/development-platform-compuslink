package com.compuslink.commonservice.dto;
import com.compuslink.commonservice.model.TargetType;
import java.time.OffsetDateTime; import java.util.UUID;
public record SavedItemResponse(UUID id, TargetType targetType, UUID targetId, OffsetDateTime createdAt) {}
