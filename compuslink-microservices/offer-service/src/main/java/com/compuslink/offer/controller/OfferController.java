package com.compuslink.offer.controller;

import com.compuslink.offer.dto.*;
import com.compuslink.offer.model.*;
import com.compuslink.offer.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest request,
                                                @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(request, userId));
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
    public List<OfferSummaryResponse> getMyOffers(@RequestHeader("X-User-Id") UUID userId) {
        return offerService.getMyOffers(userId);
    }

    @GetMapping("/{id}")
    public OfferResponse getById(@PathVariable UUID id,
                                 @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return offerService.getById(id, userId);
    }

    @PatchMapping("/{id}/close")
    public OfferResponse close(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return offerService.close(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        offerService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
