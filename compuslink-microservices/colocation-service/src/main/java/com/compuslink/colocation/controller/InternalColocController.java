package com.compuslink.colocation.controller;

import com.compuslink.colocation.service.ColocPostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Internal endpoints called by other microservices via Feign (not exposed through the gateway).
// Kept in a separate controller without the /api/coloc class-level prefix so the path
// matches the Feign client exactly: /internal/coloc/{id}/exists
@RestController
public class InternalColocController {

    private final ColocPostService service;

    public InternalColocController(ColocPostService service) {
        this.service = service;
    }

    @GetMapping("/internal/coloc/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return service.existsById(id);
    }
}
