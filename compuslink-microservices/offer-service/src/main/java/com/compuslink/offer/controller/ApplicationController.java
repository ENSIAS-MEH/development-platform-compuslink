package com.compuslink.offer.controller;

import com.compuslink.offer.dto.ApplicationResponse;
import com.compuslink.offer.dto.ApplyRequest;
import com.compuslink.offer.dto.UpdateApplicationStatusRequest;
import com.compuslink.offer.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/api/offers/{offerId}/applications")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable UUID offerId,
                                                     @Valid @RequestBody ApplyRequest request,
                                                     @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(applicationService.apply(offerId, request, userId), HttpStatus.CREATED);
    }

    @GetMapping("/api/offers/{offerId}/applications")
    public List<ApplicationResponse> getByOffer(@PathVariable UUID offerId,
                                                @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getByOffer(offerId, userId);
    }

    @GetMapping("/api/applications/mine")
    public List<ApplicationResponse> myApplications(@RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getMyApplications(userId);
    }

    @PatchMapping("/api/applications/{id}/status")
    public ApplicationResponse updateStatus(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateApplicationStatusRequest request,
                                            @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.updateStatus(id, request, userId);
    }
}
