package com.compuslink.event.controller;

import com.compuslink.event.dto.*;
import com.compuslink.event.model.EventCategory;
import com.compuslink.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                     @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, userId));
    }

    @GetMapping
    public List<EventResponse> browse(@RequestParam(required = false) EventCategory category,
                                      @RequestParam(required = false) String city,
                                      @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return eventService.browseEvents(category, city, userId);
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable UUID id,
                                  @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return eventService.getEvent(id, userId);
    }

    @PostMapping("/{id}/join")
    public EventResponse join(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return eventService.join(id, userId);
    }

    @DeleteMapping("/{id}/leave")
    public EventResponse leave(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return eventService.leave(id, userId);
    }

    @PatchMapping("/{id}/cancel")
    public EventResponse cancelEvent(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return eventService.cancelEvent(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-events")
    public List<EventResponse> myEvents(@RequestHeader("X-User-Id") UUID userId) {
        return eventService.getMyEvents(userId);
    }

    @GetMapping("/my-participations")
    public List<EventResponse> myParticipations(@RequestHeader("X-User-Id") UUID userId) {
        return eventService.getMyParticipations(userId);
    }

    @GetMapping("/{id}/participants")
    public List<Map<String, Object>> getParticipants(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return eventService.getParticipants(id, userId);
    }
}
