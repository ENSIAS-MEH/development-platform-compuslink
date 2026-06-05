package com.compuslink.offer.service;

import com.compuslink.common.exception.*;
import com.compuslink.offer.dto.*;
import com.compuslink.offer.model.*;
import com.compuslink.offer.repository.ApplicationRepository;
import com.compuslink.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepo;
    private final ApplicationRepository applicationRepo;

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
        return toResponse(offerRepo.save(offer), 0);
    }

    public Page<OfferSummaryResponse> list(OfferType type, String city, String domain,
                                           LocationType locationType, OfferStatus status,
                                           int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return offerRepo.findFiltered(type, city, domain, locationType, status, pageable)
                .map(this::toSummary);
    }

    public List<OfferSummaryResponse> getMyOffers(UUID posterId) {
        return offerRepo.findByPosterId(posterId).stream()
                .map(this::toSummary).toList();
    }

    public OfferResponse getById(UUID offerId, UUID currentUserId) {
        Offer offer = findOrThrow(offerId);
        long count = currentUserId != null && offer.getPosterId().equals(currentUserId)
                ? applicationRepo.countByOfferId(offerId) : -1;
        return toResponse(offer, count);
    }

    public OfferResponse close(UUID offerId, UUID currentUserId) {
        Offer offer = findOrThrow(offerId);
        checkOwnership(offer, currentUserId);
        if (offer.getStatus() == OfferStatus.CLOSED)
            throw new BusinessRuleException("Offer is already closed");
        offer.setStatus(OfferStatus.CLOSED);
        return toResponse(offerRepo.save(offer), applicationRepo.countByOfferId(offerId));
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
        if (!offer.getPosterId().equals(currentUserId))
            throw new AccessDeniedException("You do not own this offer");
    }

    private OfferResponse toResponse(Offer o, long applicationCount) {
        return OfferResponse.builder()
                .id(o.getId()).posterId(o.getPosterId()).type(o.getType())
                .title(o.getTitle()).company(o.getCompany()).city(o.getCity())
                .locationType(o.getLocationType()).experienceLevel(o.getExperienceLevel())
                .duration(o.getDuration()).description(o.getDescription())
                .domain(o.getDomain()).deadline(o.getDeadline()).status(o.getStatus())
                .createdAt(o.getCreatedAt()).updatedAt(o.getUpdatedAt())
                .applicationCount(applicationCount).build();
    }

    private OfferSummaryResponse toSummary(Offer o) {
        return OfferSummaryResponse.builder()
                .id(o.getId()).posterId(o.getPosterId()).type(o.getType())
                .title(o.getTitle()).company(o.getCompany()).city(o.getCity())
                .locationType(o.getLocationType()).experienceLevel(o.getExperienceLevel())
                .duration(o.getDuration()).domain(o.getDomain())
                .deadline(o.getDeadline()).status(o.getStatus())
                .createdAt(o.getCreatedAt()).build();
    }
}
