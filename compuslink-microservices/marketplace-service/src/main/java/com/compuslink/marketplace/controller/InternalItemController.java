package com.compuslink.marketplace.controller;

import com.compuslink.marketplace.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Internal endpoints called by other microservices via Feign (not exposed through the gateway).
// Kept in a separate controller without the /api/items class-level prefix so the path
// matches the Feign client exactly: /internal/items/{id}/exists
@RestController
public class InternalItemController {

    private final ItemService itemService;

    public InternalItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/internal/items/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return itemService.existsById(id);
    }
}
