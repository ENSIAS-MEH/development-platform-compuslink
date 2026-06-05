package com.CompusLink.CompusLink.domain.event.controller;

import com.CompusLink.CompusLink.domain.marketplace.service.FileStorageService;
import com.CompusLink.CompusLink.domain.event.dto.CreateEventRequest;
import com.CompusLink.CompusLink.domain.event.dto.EventResponse;
import com.CompusLink.CompusLink.domain.event.model.EventCategory;
import com.CompusLink.CompusLink.domain.event.service.EventService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadCover(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        String url = fileStorageService.store(file);
        eventService.updateCoverUrl(id, url, principal.getUser().getId());
        return Map.of("coverUrl", url);
    }

    @GetMapping
    public List<EventResponse> browse(
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String city,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID userId = principal != null ? principal.getUser().getId() : null;
        return eventService.browseEvents(category, city, userId);
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID userId = principal != null ? principal.getUser().getId() : null;
        return eventService.getEvent(id, userId);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(request, principal.getUser().getId()));
    }

    @PostMapping("/{id}/join")
    public EventResponse join(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return eventService.join(id, principal.getUser().getId());
    }

    @DeleteMapping("/{id}/leave")
    public EventResponse leave(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return eventService.leave(id, principal.getUser().getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        eventService.deleteEvent(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public EventResponse cancelEvent(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return eventService.cancelEvent(id, principal.getUser().getId());
    }

    @GetMapping("/my-participations")
    public List<EventResponse> myParticipations(@AuthenticationPrincipal UserPrincipal principal) {
        return eventService.getMyParticipations(principal.getUser().getId());
    }

    @GetMapping("/{id}/participants")
    public List<Map<String, Object>> getParticipants(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return eventService.getParticipants(id, principal.getUser().getId());
    }

    @GetMapping("/my-events")
    public List<EventResponse> myEvents(@AuthenticationPrincipal UserPrincipal principal) {
        return eventService.getMyEvents(principal.getUser().getId());
    }
}
