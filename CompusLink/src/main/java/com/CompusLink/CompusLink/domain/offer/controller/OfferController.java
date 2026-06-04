package com.CompusLink.CompusLink.domain.offer.controller;

import com.CompusLink.CompusLink.domain.offer.dto.CreateOfferRequest;
import com.CompusLink.CompusLink.domain.offer.dto.OfferResponse;
import com.CompusLink.CompusLink.domain.offer.dto.OfferSummaryResponse;
import com.CompusLink.CompusLink.domain.offer.model.LocationType;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import com.CompusLink.CompusLink.domain.offer.service.OfferService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest request,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        OfferResponse response = offerService.create(request, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<OfferSummaryResponse> list(@RequestParam(required = false) OfferType type,
                                           @RequestParam(required = false) String city,
                                           @RequestParam(required = false) String domain,
                                           @RequestParam(required = false) LocationType locationType,
                                           @RequestParam(required = false) OfferStatus status,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return offerService.list(type, city, domain, locationType, status, page, size);
    }

    @GetMapping("/my-offers")
    public java.util.List<OfferSummaryResponse> getMyOffers(@AuthenticationPrincipal UserPrincipal principal) {
        return offerService.getMyOffers(principal.getUser().getId());
    }

    @GetMapping("/{id}")
    public OfferResponse getById(@PathVariable UUID id,
                                 @AuthenticationPrincipal UserPrincipal principal) {
        UUID currentUserId = principal != null ? principal.getUser().getId() : null;
        return offerService.getById(id, currentUserId);
    }

    @PatchMapping("/{id}/close")
    public OfferResponse close(@PathVariable UUID id,
                               @AuthenticationPrincipal UserPrincipal principal) {
        return offerService.close(id, principal.getUser().getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        offerService.delete(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
