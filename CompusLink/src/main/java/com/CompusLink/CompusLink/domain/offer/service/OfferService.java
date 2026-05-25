package com.CompusLink.CompusLink.domain.offer.service;

import com.CompusLink.CompusLink.domain.offer.dto.CreateOfferRequest;
import com.CompusLink.CompusLink.domain.offer.dto.OfferResponse;
import com.CompusLink.CompusLink.domain.offer.dto.OfferSummaryResponse;
import com.CompusLink.CompusLink.domain.offer.model.LocationType;
import com.CompusLink.CompusLink.domain.offer.model.Offer;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import com.CompusLink.CompusLink.domain.offer.repository.ApplicationRepository;
import com.CompusLink.CompusLink.domain.offer.repository.OfferRepository;
import com.CompusLink.CompusLink.exception.AccessDeniedException;
import com.CompusLink.CompusLink.exception.BusinessRuleException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OfferService {

    private final OfferRepository offerRepo;
    private final ApplicationRepository applicationRepo;

    public OfferService(OfferRepository offerRepo, ApplicationRepository applicationRepo) {
        this.offerRepo = offerRepo;
        this.applicationRepo = applicationRepo;
    }

    public OfferResponse create(CreateOfferRequest request, UUID posterId) {
        Offer offer = Offer.builder()
                .posterId(posterId)
                .type(request.getType())
                .title(request.getTitle())
                .company(request.getCompany())
                .city(request.getCity())
                .locationType(request.getLocationType())
                .experienceLevel(request.getExperienceLevel())
                .duration(request.getDuration())
                .description(request.getDescription())
                .domain(request.getDomain())
                .deadline(request.getDeadline())
                .build();

        Offer saved = offerRepo.save(offer);
        return toResponse(saved, 0);
    }

    public Page<OfferSummaryResponse> list(OfferType type, String city, String domain,
                                           LocationType locationType, OfferStatus status,
                                           int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return offerRepo.findFiltered(type, city, domain, locationType, status, pageable)
                .map(this::toSummary);
    }

    public OfferResponse getById(UUID offerId, UUID currentUserId) {
        Offer offer = findOrThrow(offerId);
        long count = offer.getPosterId().equals(currentUserId)
                ? applicationRepo.countByOfferId(offerId)
                : -1;
        return toResponse(offer, count);
    }

    public OfferResponse close(UUID offerId, UUID currentUserId) {
        Offer offer = findOrThrow(offerId);
        checkOwnership(offer, currentUserId);

        if (offer.getStatus() == OfferStatus.CLOSED) {
            throw new BusinessRuleException("Offer is already closed");
        }

        offer.setStatus(OfferStatus.CLOSED);
        Offer saved = offerRepo.save(offer);
        return toResponse(saved, applicationRepo.countByOfferId(offerId));
    }

    @Transactional
    public void delete(UUID offerId, UUID currentUserId) {
        Offer offer = findOrThrow(offerId);
        checkOwnership(offer, currentUserId);
        applicationRepo.deleteByOfferId(offerId);
        offerRepo.delete(offer);
    }

    private Offer findOrThrow(UUID offerId) {
        return offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
    }

    private void checkOwnership(Offer offer, UUID currentUserId) {
        if (!offer.getPosterId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this offer");
        }
    }

    private OfferResponse toResponse(Offer o, long applicationCount) {
        return OfferResponse.builder()
                .id(o.getId())
                .posterId(o.getPosterId())
                .type(o.getType())
                .title(o.getTitle())
                .company(o.getCompany())
                .city(o.getCity())
                .locationType(o.getLocationType())
                .experienceLevel(o.getExperienceLevel())
                .duration(o.getDuration())
                .description(o.getDescription())
                .domain(o.getDomain())
                .deadline(o.getDeadline())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .applicationCount(applicationCount)
                .build();
    }

    private OfferSummaryResponse toSummary(Offer o) {
        return OfferSummaryResponse.builder()
                .id(o.getId())
                .posterId(o.getPosterId())
                .type(o.getType())
                .title(o.getTitle())
                .company(o.getCompany())
                .city(o.getCity())
                .locationType(o.getLocationType())
                .experienceLevel(o.getExperienceLevel())
                .duration(o.getDuration())
                .domain(o.getDomain())
                .deadline(o.getDeadline())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
