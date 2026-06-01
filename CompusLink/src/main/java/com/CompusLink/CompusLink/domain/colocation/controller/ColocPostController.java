package com.CompusLink.CompusLink.domain.colocation.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.CompusLink.CompusLink.domain.colocation.dto.ColocPostDTO;
import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import com.CompusLink.CompusLink.domain.colocation.model.HousingType;
import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;
import com.CompusLink.CompusLink.domain.colocation.service.ColocPostService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/colocations")
@RequiredArgsConstructor
public class ColocPostController {

    private final ColocPostService postService;

    @GetMapping
    public ResponseEntity<Page<ColocPostDTO>> browsePosts(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) HousingType type,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false) BigDecimal rentMax,
            @RequestParam(required = false) ColocStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(postService.browsePosts(city, type, furnished, rentMax, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColocPostDTO> getPostDetails(@PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        // CORRECTION : On vérifie si principal est nul avant d'appeler .getUser()
        UUID currentUserId = (principal != null) ? principal.getUser().getId() : null;

        return ResponseEntity.ok(postService.getPostDetails(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<ColocPostDTO> createPost(@RequestBody ColocPostDTO postDTO,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(postService.createPost(postDTO, principal.getUser().getId()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/spots")
    public ResponseEntity<Void> updateSpots(@PathVariable UUID id,
                                            @RequestParam int count,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        postService.updateSpotsConfirmed(id, count, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/interests")
    public ResponseEntity<Void> expressInterest(@PathVariable UUID id,
                                                @RequestParam String message,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        postService.expressInterest(id, message, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/interests/{interestId}/status")
    public ResponseEntity<Void> handleInterest(@PathVariable UUID interestId,
                                               @RequestParam InterestStatus status,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        postService.handleInterestStatus(interestId, status, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(@PathVariable UUID id,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam(defaultValue = "false") boolean isCover,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        postService.uploadPhoto(id, file, isCover, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
