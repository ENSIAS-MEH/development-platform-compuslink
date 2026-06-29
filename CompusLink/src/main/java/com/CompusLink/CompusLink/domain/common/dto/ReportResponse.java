package com.CompusLink.CompusLink.domain.common.dto;

import com.CompusLink.CompusLink.domain.common.model.ReportReason;
import com.CompusLink.CompusLink.domain.common.model.ReportStatus;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private UUID id;
    private UUID reporterId;
    private TargetType targetType;
    private UUID targetId;
    private ReportReason reason;
    private String details;
    private ReportStatus status;
    private OffsetDateTime createdAt;
}
