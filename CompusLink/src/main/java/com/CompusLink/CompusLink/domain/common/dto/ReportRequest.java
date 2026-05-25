package com.CompusLink.CompusLink.domain.common.dto;

import com.CompusLink.CompusLink.domain.common.model.ReportReason;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ReportRequest {

    @NotNull
    private TargetType targetType;

    @NotNull
    private UUID targetId;

    @NotNull
    private ReportReason reason;

    @Size(max = 500)
    private String details;
}
