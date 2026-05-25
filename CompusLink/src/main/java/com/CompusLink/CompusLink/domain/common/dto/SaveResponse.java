package com.CompusLink.CompusLink.domain.common.dto;

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
public class SaveResponse {

    private UUID id;
    private TargetType targetType;
    private UUID targetId;
    private OffsetDateTime createdAt;
}
