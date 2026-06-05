package com.compuslink.offer.dto;

import com.compuslink.offer.model.AppStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private UUID id;
    private UUID offerId;
    private UUID applicantId;
    private String applicantEmail;
    private String applicantName;
    private String cvUrlSnapshot;
    private String message;
    private AppStatus status;
    private OffsetDateTime appliedAt;
    private OffsetDateTime updatedAt;

    // Offer details
    private String offerTitle;
    private String offerCompany;
    private String offerType;
}
