package com.CompusLink.CompusLink.domain.offer.service;

import com.CompusLink.CompusLink.domain.offer.dto.ApplicationResponse;
import com.CompusLink.CompusLink.domain.offer.dto.ApplyRequest;
import com.CompusLink.CompusLink.domain.offer.dto.UpdateApplicationStatusRequest;
import com.CompusLink.CompusLink.domain.offer.model.AppStatus;
import com.CompusLink.CompusLink.domain.offer.model.Application;
import com.CompusLink.CompusLink.domain.offer.model.Offer;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.repository.ApplicationRepository;
import com.CompusLink.CompusLink.domain.offer.repository.OfferRepository;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.exception.AccessDeniedException;
import com.CompusLink.CompusLink.exception.BusinessRuleException;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepo;
    private final OfferRepository offerRepo;
    private final UserRepository userRepo;

    public ApplicationService(ApplicationRepository applicationRepo,
                              OfferRepository offerRepo,
                              UserRepository userRepo) {
        this.applicationRepo = applicationRepo;
        this.offerRepo = offerRepo;
        this.userRepo = userRepo;
    }

    public ApplicationResponse apply(UUID offerId, ApplyRequest request, UUID applicantId) {
        Offer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        if (offer.getStatus() != OfferStatus.OPEN) {
            throw new BusinessRuleException("Offer is not open for applications");
        }

        if (offer.getPosterId().equals(applicantId)) {
            throw new BusinessRuleException("You cannot apply to your own offer");
        }

        if (applicationRepo.existsByOfferIdAndApplicantId(offerId, applicantId)) {
            throw new DuplicateResourceException("You have already applied to this offer");
        }

        Users applicant = userRepo.findById(applicantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (applicant.getCvUrl() == null || applicant.getCvUrl().isBlank()) {
            throw new BusinessRuleException("Please upload a CV to your profile before applying");
        }

        Application application = Application.builder()
                .offerId(offerId)
                .applicantId(applicantId)
                .cvUrlSnapshot(applicant.getCvUrl())
                .message(request.getMessage())
                .build();

        return toResponse(applicationRepo.save(application));
    }

    @Transactional
    public List<ApplicationResponse> getByOffer(UUID offerId, UUID currentUserId) {
        Offer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        if (!offer.getPosterId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this offer");
        }

        List<Application> pending = applicationRepo.findByOfferIdAndStatus(offerId, AppStatus.PENDING);
        for (Application app : pending) {
            app.setStatus(AppStatus.SEEN);
        }
        applicationRepo.saveAll(pending);

        return applicationRepo.findByOfferId(offerId).stream()
                .map(a -> {
                    ApplicationResponse resp = toResponse(a);
                    userRepo.findById(a.getApplicantId())
                            .ifPresent(u -> {
                                resp.setApplicantEmail(u.getEmail());
                                resp.setApplicantName(u.getFullName());
                            });
                    return resp;
                })
                .toList();
    }

    public List<ApplicationResponse> getMyApplications(UUID applicantId) {
        return applicationRepo.findByApplicantId(applicantId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ApplicationResponse updateStatus(UUID applicationId, UpdateApplicationStatusRequest request, UUID currentUserId) {
        if (request.getStatus() != AppStatus.ACCEPTED && request.getStatus() != AppStatus.REJECTED) {
            throw new BusinessRuleException("Status can only be changed to ACCEPTED or REJECTED");
        }

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        Offer offer = offerRepo.findById(application.getOfferId())
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        if (!offer.getPosterId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this offer");
        }

        application.setStatus(request.getStatus());
        return toResponse(applicationRepo.save(application));
    }

    private ApplicationResponse toResponse(Application a) {
        ApplicationResponse.ApplicationResponseBuilder builder = ApplicationResponse.builder()
                .id(a.getId())
                .offerId(a.getOfferId())
                .applicantId(a.getApplicantId())
                .cvUrlSnapshot(a.getCvUrlSnapshot())
                .message(a.getMessage())
                .status(a.getStatus())
                .appliedAt(a.getAppliedAt())
                .updatedAt(a.getUpdatedAt());

        offerRepo.findById(a.getOfferId()).ifPresent(offer -> {
            builder.offerTitle(offer.getTitle());
            builder.offerCompany(offer.getCompany());
            builder.offerType(offer.getType().toString());
        });

        return builder.build();
    }
}
