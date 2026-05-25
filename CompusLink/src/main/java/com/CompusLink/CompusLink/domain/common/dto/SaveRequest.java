package com.CompusLink.CompusLink.domain.common.dto;

import com.CompusLink.CompusLink.domain.common.model.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveRequest {

    @NotNull
    private TargetType targetType;

    @NotNull
    private UUID targetId;
}
