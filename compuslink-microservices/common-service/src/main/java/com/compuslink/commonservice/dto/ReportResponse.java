package com.compuslink.commonservice.dto;
import com.compuslink.commonservice.model.*;
import java.time.OffsetDateTime; import java.util.UUID;
public record ReportResponse(UUID id, TargetType targetType, UUID targetId, ReportReason reason, ReportStatus status, OffsetDateTime createdAt) {}
