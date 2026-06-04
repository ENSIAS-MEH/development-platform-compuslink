package com.compuslink.offer.dto;

import com.compuslink.offer.model.AppStatus;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class ApplicationResponse {
    private UUID id;
    private UUID offerId;
    private UUID applicantId;
    private String applicantName;
    private String cvUrlSnapshot;
    private String message;
    private AppStatus status;
    private OffsetDateTime appliedAt;
}
