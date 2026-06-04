package com.compuslink.marketplace.controller;

import com.compuslink.marketplace.dto.*;
import com.compuslink.marketplace.model.ItemCondition;
import com.compuslink.marketplace.model.ItemStatus;
import com.compuslink.marketplace.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemResponse create(@Valid @ModelAttribute ItemCreateRequest request,
                               @RequestHeader("X-User-Id") UUID userId) {
        return itemService.createItem(request, userId);
    }

    @GetMapping
    public List<ItemSummaryResponse> browse(@RequestParam(required = false) String search,
                                            @RequestParam(required = false) String city,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) ItemCondition condition,
                                            @RequestParam(required = false) ItemStatus status) {
        return itemService.browseItems(search, city, category, condition, status);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable UUID id) {
        return itemService.getItem(id);
    }

    @PatchMapping("/{id}")
    public ItemResponse update(@PathVariable UUID id, @ModelAttribute ItemUpdateRequest request,
                               @RequestHeader("X-User-Id") UUID userId) {
        return itemService.updateItem(id, request, userId);
    }

    @PatchMapping("/{id}/sold")
    public ItemResponse markAsSold(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return itemService.markAsSold(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> close(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        itemService.closeItem(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/interest")
    public ItemInterestResponse expressInterest(@PathVariable UUID id,
                                                @RequestBody ItemInterestRequest request,
                                                @RequestHeader("X-User-Id") UUID userId) {
        return itemService.expressInterest(id, request, userId);
    }

    @GetMapping("/{id}/interests")
    public List<ItemInterestResponse> getInterests(@PathVariable UUID id,
                                                   @RequestHeader("X-User-Id") UUID userId) {
        return itemService.getItemInterests(id, userId);
    }

    @GetMapping("/mine")
    public List<ItemSummaryResponse> myItems(@RequestHeader("X-User-Id") UUID userId) {
        return itemService.getMyItems(userId);
    }

    // Internal endpoint for common-service
    @GetMapping("/internal/items/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return itemService.existsById(id);
    }
}
