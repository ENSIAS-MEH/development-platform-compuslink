package com.compuslink.offer.service;

import com.compuslink.common.exception.*;
import com.compuslink.offer.client.UserClient;
import com.compuslink.offer.dto.*;
import com.compuslink.offer.model.*;
import com.compuslink.offer.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class OfferService {

    private final OfferRepository offerRepo;
    private final ApplicationRepository appRepo;
    private final UserClient userClient;

    public OfferResponse createOffer(CreateOfferRequest req, UUID posterId) {
        Offer offer = offerRepo.save(Offer.builder().posterId(posterId).type(req.getType())
                .title(req.getTitle()).company(req.getCompany()).city(req.getCity())
                .locationType(req.getLocationType()).experienceLevel(req.getExperienceLevel())
                .duration(req.getDuration()).description(req.getDescription())
                .domain(req.getDomain()).deadline(req.getDeadline()).build());
        return toResponse(offer);
    }

    public List<OfferResponse> browseOffers(OfferType type) {
        List<Offer> offers = type == null ? offerRepo.findByStatus(OfferStatus.OPEN)
                : offerRepo.findByTypeAndStatus(type, OfferStatus.OPEN);
        return offers.stream().map(this::toResponse).toList();
    }

    public OfferResponse getOffer(UUID id) {
        return toResponse(offerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Offer not found")));
    }

    public List<OfferResponse> getMyOffers(UUID posterId) {
        return offerRepo.findByPosterId(posterId).stream().map(this::toResponse).toList();
    }

    public void closeOffer(UUID id, UUID userId) {
        Offer offer = offerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (!offer.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        offer.setStatus(OfferStatus.CLOSED);
        offerRepo.save(offer);
    }

    public ApplicationResponse apply(UUID offerId, ApplyRequest req, UUID applicantId) {
        Offer offer = offerRepo.findById(offerId).orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (offer.getStatus() != OfferStatus.OPEN) throw new BusinessRuleException("Offer is closed");
        if (offer.getPosterId().equals(applicantId)) throw new BusinessRuleException("Cannot apply to own offer");
        if (appRepo.existsByOfferIdAndApplicantId(offerId, applicantId)) throw new DuplicateResourceException("Already applied");
        Application app = appRepo.save(Application.builder().offerId(offerId).applicantId(applicantId)
                .cvUrlSnapshot(req.getCvUrlSnapshot()).message(req.getMessage()).build());
        return toAppResponse(app);
    }

    public List<ApplicationResponse> getApplications(UUID offerId, UUID userId) {
        Offer offer = offerRepo.findById(offerId).orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (!offer.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        return appRepo.findByOfferId(offerId).stream().map(this::toAppResponse).toList();
    }

    public void updateApplicationStatus(UUID appId, AppStatus status, UUID userId) {
        Application app = appRepo.findById(appId).orElseThrow(() -> new EntityNotFoundException("Application not found"));
        Offer offer = offerRepo.findById(app.getOfferId()).orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        if (!offer.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        app.setStatus(status);
        appRepo.save(app);
    }

    public boolean existsById(UUID id) { return offerRepo.existsById(id); }

    private OfferResponse toResponse(Offer o) {
        String name = getName(o.getPosterId());
        return OfferResponse.builder().id(o.getId()).posterId(o.getPosterId()).posterName(name)
                .type(o.getType()).title(o.getTitle()).company(o.getCompany()).city(o.getCity())
                .locationType(o.getLocationType()).experienceLevel(o.getExperienceLevel())
                .duration(o.getDuration()).description(o.getDescription()).domain(o.getDomain())
                .deadline(o.getDeadline()).status(o.getStatus()).createdAt(o.getCreatedAt()).build();
    }

    private ApplicationResponse toAppResponse(Application a) {
        return ApplicationResponse.builder().id(a.getId()).offerId(a.getOfferId()).applicantId(a.getApplicantId())
                .applicantName(getName(a.getApplicantId())).cvUrlSnapshot(a.getCvUrlSnapshot())
                .message(a.getMessage()).status(a.getStatus()).appliedAt(a.getAppliedAt()).build();
    }

    private String getName(UUID userId) {
        try { return userClient.getUserSummary(userId).getFullName(); } catch (Exception e) { return "Unknown"; }
    }
}
