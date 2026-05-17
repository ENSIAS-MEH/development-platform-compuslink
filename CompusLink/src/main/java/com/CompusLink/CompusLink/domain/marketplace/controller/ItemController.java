package com.CompusLink.CompusLink.domain.marketplace.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CompusLink.CompusLink.domain.marketplace.dto.ItemCreateRequest;
import com.CompusLink.CompusLink.domain.marketplace.dto.ItemInterestRequest;
import com.CompusLink.CompusLink.domain.marketplace.dto.ItemInterestResponse;
import com.CompusLink.CompusLink.domain.marketplace.dto.ItemResponse;
import com.CompusLink.CompusLink.domain.marketplace.dto.ItemSummaryResponse;
import com.CompusLink.CompusLink.domain.marketplace.dto.ItemUpdateRequest;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import com.CompusLink.CompusLink.domain.marketplace.service.ItemService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/marketplace")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping(value = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> createItem(
            @Valid @ModelAttribute ItemCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(request, principal.getUser().getId()));
    }

    @GetMapping("/items")
    public List<ItemSummaryResponse> browseItems(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ItemCondition condition,
            @RequestParam(required = false) ItemStatus status) {
        return itemService.browseItems(city, category, condition, status);
    }

    @GetMapping("/items/{id}")
    public ItemResponse getItem(@PathVariable UUID id) {
        return itemService.getItem(id);
    }

    @PutMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ItemResponse updateItem(
            @PathVariable UUID id,
            @ModelAttribute ItemUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return itemService.updateItem(id, request, principal.getUser().getId());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> closeItem(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        itemService.closeItem(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/items/{id}/sold")
    public ItemResponse markAsSold(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return itemService.markAsSold(id, principal.getUser().getId());
    }

    @PostMapping("/items/{id}/interest")
    public ResponseEntity<ItemInterestResponse> expressInterest(
            @PathVariable UUID id,
            @RequestBody ItemInterestRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.expressInterest(id, request, principal.getUser().getId()));
    }

    @GetMapping("/items/{id}/interests")
    public List<ItemInterestResponse> getItemInterests(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return itemService.getItemInterests(id, principal.getUser().getId());
    }

    @GetMapping("/my-items")
    public List<ItemSummaryResponse> getMyItems(@AuthenticationPrincipal UserPrincipal principal) {
        return itemService.getMyItems(principal.getUser().getId());
    }

    @GetMapping("/my-interests")
    public List<ItemSummaryResponse> getMyInterests(@AuthenticationPrincipal UserPrincipal principal) {
        return itemService.getMyInterests(principal.getUser().getId());
    }
}
