package com.compuslink.offer.service;

import com.compuslink.common.exception.*;
import com.compuslink.offer.dto.*;
import com.compuslink.offer.model.*;
import com.compuslink.offer.repository.ApplicationRepository;
import com.compuslink.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepo;
    private final OfferRepository offerRepo;

    public ApplicationResponse apply(UUID offerId, ApplyRequest request, UUID applicantId) {
        Offer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        if (offer.getStatus() != OfferStatus.OPEN)
            throw new BusinessRuleException("Offer is not open for applications");
        if (offer.getPosterId().equals(applicantId))
            throw new BusinessRuleException("You cannot apply to your own offer");
        if (applicationRepo.existsByOfferIdAndApplicantId(offerId, applicantId))
            throw new DuplicateResourceException("You have already applied to this offer");

        // In microservice context, cvUrl is provided directly by the frontend/gateway
        String cvUrl = request.getCvUrl();
        if (cvUrl == null || cvUrl.isBlank())
            throw new BusinessRuleException("Please upload a CV to your profile before applying");

        Application application = Application.builder()
                .offerId(offerId)
                .applicantId(applicantId)
                .cvUrlSnapshot(cvUrl)
                .message(request.getMessage())
                .build();

        return toResponse(applicationRepo.save(application));
    }

    @Transactional
    public List<ApplicationResponse> getByOffer(UUID offerId, UUID currentUserId) {
        Offer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (!offer.getPosterId().equals(currentUserId))
            throw new AccessDeniedException("You do not own this offer");

        List<Application> pending = applicationRepo.findByOfferIdAndStatus(offerId, AppStatus.PENDING);
        for (Application app : pending) { app.setStatus(AppStatus.SEEN); }
        applicationRepo.saveAll(pending);

        return applicationRepo.findByOfferId(offerId).stream()
                .map(this::toResponse).toList();
    }

    public List<ApplicationResponse> getMyApplications(UUID applicantId) {
        return applicationRepo.findByApplicantId(applicantId).stream()
                .map(this::toResponse).toList();
    }

    public ApplicationResponse updateStatus(UUID applicationId, UpdateApplicationStatusRequest request, UUID currentUserId) {
        if (request.getStatus() != AppStatus.ACCEPTED && request.getStatus() != AppStatus.REJECTED)
            throw new BusinessRuleException("Status can only be changed to ACCEPTED or REJECTED");

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        Offer offer = offerRepo.findById(application.getOfferId())
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (!offer.getPosterId().equals(currentUserId))
            throw new AccessDeniedException("You do not own this offer");

        application.setStatus(request.getStatus());
        return toResponse(applicationRepo.save(application));
    }

    private ApplicationResponse toResponse(Application a) {
        ApplicationResponse.ApplicationResponseBuilder builder = ApplicationResponse.builder()
                .id(a.getId()).offerId(a.getOfferId()).applicantId(a.getApplicantId())
                .cvUrlSnapshot(a.getCvUrlSnapshot()).message(a.getMessage())
                .status(a.getStatus()).appliedAt(a.getAppliedAt()).updatedAt(a.getUpdatedAt());

        offerRepo.findById(a.getOfferId()).ifPresent(offer -> {
            builder.offerTitle(offer.getTitle());
            builder.offerCompany(offer.getCompany());
            builder.offerType(offer.getType().toString());
        });

        return builder.build();
    }
}
