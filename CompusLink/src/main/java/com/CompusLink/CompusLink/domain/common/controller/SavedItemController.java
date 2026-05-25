package com.CompusLink.CompusLink.domain.common.controller;

import com.CompusLink.CompusLink.domain.common.dto.SaveRequest;
import com.CompusLink.CompusLink.domain.common.dto.SaveResponse;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import com.CompusLink.CompusLink.domain.common.service.SavedItemService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/saved")
public class SavedItemController {

    private final SavedItemService savedItemService;

    public SavedItemController(SavedItemService savedItemService) {
        this.savedItemService = savedItemService;
    }

    @PostMapping
    public ResponseEntity<SaveResponse> save(@Valid @RequestBody SaveRequest request,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        SaveResponse response = savedItemService.save(request, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{targetType}/{targetId}")
    public ResponseEntity<Void> unsave(@PathVariable TargetType targetType,
                                       @PathVariable UUID targetId,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        savedItemService.unsave(principal.getUser().getId(), targetType, targetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<SaveResponse> getMySaved(@RequestParam(required = false) TargetType targetType,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return savedItemService.getMySaved(principal.getUser().getId(), targetType);
    }
}
