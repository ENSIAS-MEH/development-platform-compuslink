package com.compuslink.offer.controller;

import com.compuslink.offer.dto.*;
import com.compuslink.offer.model.AppStatus;
import com.compuslink.offer.model.OfferType;
import com.compuslink.offer.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequiredArgsConstructor
public class OfferController {

    private final OfferService service;

    @PostMapping("/api/offers")
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(service.createOffer(req, userId), HttpStatus.CREATED);
    }

    @GetMapping("/api/offers")
    public List<OfferResponse> browse(@RequestParam(required = false) OfferType type) { return service.browseOffers(type); }

    @GetMapping("/api/offers/{id}")
    public OfferResponse get(@PathVariable UUID id) { return service.getOffer(id); }

    @GetMapping("/api/offers/mine")
    public List<OfferResponse> mine(@RequestHeader("X-User-Id") UUID userId) { return service.getMyOffers(userId); }

    @PatchMapping("/api/offers/{id}/close")
    public ResponseEntity<Void> close(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        service.closeOffer(id, userId); return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/offers/{id}/apply")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable UUID id, @RequestBody ApplyRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(service.apply(id, req, userId), HttpStatus.CREATED);
    }

    @GetMapping("/api/offers/{id}/applications")
    public List<ApplicationResponse> applications(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return service.getApplications(id, userId);
    }

    @PatchMapping("/api/applications/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam AppStatus status, @RequestHeader("X-User-Id") UUID userId) {
        service.updateApplicationStatus(id, status, userId); return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/offers/{id}/exists")
    public boolean exists(@PathVariable UUID id) { return service.existsById(id); }
}
