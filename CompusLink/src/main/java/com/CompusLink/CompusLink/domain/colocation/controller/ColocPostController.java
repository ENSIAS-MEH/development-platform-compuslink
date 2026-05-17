package com.CompusLink.CompusLink.domain.colocation.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.CompusLink.CompusLink.domain.colocation.dto.ColocPostDTO;
import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import com.CompusLink.CompusLink.domain.colocation.model.HousingType;
import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;
import com.CompusLink.CompusLink.domain.colocation.service.ColocPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/colocations")
@RequiredArgsConstructor
public class ColocPostController {

    private final ColocPostService postService;

    // --- Gestion des Annonces (Posts) ---

    @GetMapping
    public ResponseEntity<Page<ColocPostDTO>> browsePosts(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) HousingType type,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false) BigDecimal rentMax,
            @RequestParam(required = false) ColocStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        // Utilise la méthode de filtrage et pagination du service
        return ResponseEntity.ok(postService.browsePosts(city, type, furnished, rentMax, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColocPostDTO> getPostDetails(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID currentUserId) {
        // currentUserId permet de gérer la visibilité conditionnelle des intérêts (Task 5.3)
        return ResponseEntity.ok(postService.getPostDetails(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<ColocPostDTO> createPost(@RequestBody ColocPostDTO postDTO, @RequestHeader("X-User-Id") UUID currentUserId) {
        return new ResponseEntity<>(postService.createPost(postDTO, currentUserId), HttpStatus.CREATED);
    }

    // --- Gestion des Places et Statuts ---

    @PatchMapping("/{id}/spots")
    public ResponseEntity<Void> updateSpots(@PathVariable UUID id, @RequestParam int count, @RequestHeader("X-User-Id") UUID currentUserId) {
        postService.updateSpotsConfirmed(id, count, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // --- Gestion des Intérêts ---

    @PostMapping("/{id}/interests")
    public ResponseEntity<Void> expressInterest(@PathVariable UUID id, @RequestParam String message, @RequestHeader("X-User-Id") UUID currentUserId) {
        postService.expressInterest(id, message, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/interests/{interestId}/status")
    public ResponseEntity<Void> handleInterest(@PathVariable UUID interestId, @RequestParam InterestStatus status, @RequestHeader("X-User-Id") UUID currentUserId) {
        postService.handleInterestStatus(interestId, status, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // --- Gestion des Photos ---

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isCover,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        postService.uploadPhoto(id, file, isCover, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}