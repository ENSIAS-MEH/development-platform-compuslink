package com.compuslink.event.controller;

import com.compuslink.event.dto.*;
import com.compuslink.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/events") @RequiredArgsConstructor
public class EventController {
    private final EventService service;

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(service.createEvent(req, userId), HttpStatus.CREATED);
    }
    @GetMapping public List<EventResponse> all() { return service.getAllEvents(); }
    @GetMapping("/{id}") public EventResponse get(@PathVariable UUID id) { return service.getEvent(id); }
    @GetMapping("/mine") public List<EventResponse> mine(@RequestHeader("X-User-Id") UUID userId) { return service.getMyEvents(userId); }
    @PatchMapping("/{id}/cancel") public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) { service.cancelEvent(id, userId); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/join") public ResponseEntity<Void> join(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) { service.joinEvent(id, userId); return ResponseEntity.status(HttpStatus.CREATED).build(); }
    @DeleteMapping("/{id}/leave") public ResponseEntity<Void> leave(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) { service.leaveEvent(id, userId); return ResponseEntity.noContent().build(); }
}
