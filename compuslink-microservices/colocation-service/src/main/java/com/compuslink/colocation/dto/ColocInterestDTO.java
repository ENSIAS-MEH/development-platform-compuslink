package com.compuslink.colocation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.compuslink.colocation.model.InterestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ColocInterestDTO {
    private UUID id;
    private UUID userId;
    private String userName;
    private String message;
    private InterestStatus status;
    private OffsetDateTime createdAt;
}
