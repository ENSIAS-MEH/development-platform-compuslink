package com.CompusLink.CompusLink.domain.offer.dto;

import com.CompusLink.CompusLink.domain.offer.model.AppStatus;
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
