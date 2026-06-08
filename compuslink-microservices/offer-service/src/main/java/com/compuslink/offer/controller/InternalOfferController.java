package com.compuslink.offer.controller;

import com.compuslink.offer.service.OfferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Internal endpoints called by other microservices via Feign (not exposed through the gateway).
// Kept in a separate controller without the /api/offers class-level prefix so the path
// matches the Feign client exactly: /internal/offers/{id}/exists
@RestController
public class InternalOfferController {

    private final OfferService offerService;

    public InternalOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/internal/offers/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return offerService.existsById(id);
    }
}
