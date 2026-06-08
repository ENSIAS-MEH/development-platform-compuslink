package com.compuslink.colocation.controller;

import com.compuslink.colocation.dto.ColocPostDTO;
import com.compuslink.colocation.model.HousingType;
import com.compuslink.colocation.model.InterestStatus;
import com.compuslink.colocation.service.ColocPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/coloc") @RequiredArgsConstructor
public class ColocPostController {

    private final ColocPostService service;

    @PostMapping
    public ResponseEntity<ColocPostDTO> create(@RequestBody ColocPostDTO dto, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(service.createPost(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public Page<ColocPostDTO> browse(@RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestParam(required = false) String city, @RequestParam(required = false) HousingType type,
            @RequestParam(required = false) Boolean furnished, @RequestParam(required = false) Integer spotsNeeded,
            @RequestParam(required = false) BigDecimal rentMax, Pageable pageable) {
        return service.browsePosts(userId, city, type, furnished, spotsNeeded, rentMax, pageable);
    }

    @GetMapping("/{id}")
    public ColocPostDTO getDetails(@PathVariable UUID id, @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return service.getPostDetails(id, userId);
    }

    @GetMapping("/my-posts")
    public List<ColocPostDTO> myPosts(@RequestHeader("X-User-Id") UUID userId) {
        return service.getMyPosts(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        service.deletePost(id, userId); return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/spots")
    public ResponseEntity<Void> updateSpots(@PathVariable UUID id, @RequestParam int count, @RequestHeader("X-User-Id") UUID userId) {
        service.updateSpotsConfirmed(id, count, userId); return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/interests")
    public ResponseEntity<Void> expressInterest(@PathVariable UUID id, @RequestParam String message, @RequestHeader("X-User-Id") UUID userId) {
        service.expressInterest(id, message, userId); return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/interests/{interestId}/status")
    public ResponseEntity<Void> handleInterest(@PathVariable UUID interestId, @RequestParam InterestStatus status, @RequestHeader("X-User-Id") UUID userId) {
        service.handleInterestStatus(interestId, status, userId); return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhotos(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files, @RequestHeader("X-User-Id") UUID userId) {
        service.uploadPhotos(id, files, userId); return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
