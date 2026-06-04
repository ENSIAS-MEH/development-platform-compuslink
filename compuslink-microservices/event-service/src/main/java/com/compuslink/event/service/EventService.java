package com.compuslink.event.service;

import com.compuslink.common.exception.*;
import com.compuslink.event.client.UserClient;
import com.compuslink.event.dto.*;
import com.compuslink.event.model.*;
import com.compuslink.event.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class EventService {
    private final EventRepository eventRepo;
    private final EventParticipantRepository participantRepo;
    private final UserClient userClient;

    public EventResponse createEvent(CreateEventRequest req, UUID organizerId) {
        Event event = eventRepo.save(Event.builder().organizerId(organizerId).title(req.getTitle())
                .description(req.getDescription()).location(req.getLocation()).city(req.getCity())
                .eventDate(req.getEventDate()).category(req.getCategory())
                .maxParticipants(req.getMaxParticipants()).coverUrl(req.getCoverUrl()).build());
        return toResponse(event);
    }

    public List<EventResponse> getAllEvents() { return eventRepo.findAll().stream().map(this::toResponse).toList(); }
    public EventResponse getEvent(UUID id) { return toResponse(eventRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"))); }
    public List<EventResponse> getMyEvents(UUID organizerId) { return eventRepo.findByOrganizerId(organizerId).stream().map(this::toResponse).toList(); }

    public void cancelEvent(UUID id, UUID userId) {
        Event event = eventRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getOrganizerId().equals(userId)) throw new AccessDeniedException("Not authorized");
        event.setCancelled(true); eventRepo.save(event);
    }

    public void joinEvent(UUID eventId, UUID userId) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (event.isCancelled()) throw new BusinessRuleException("Event is cancelled");
        if (participantRepo.existsByEventIdAndUserId(eventId, userId)) throw new DuplicateResourceException("Already joined");
        if (event.getMaxParticipants() != null && participantRepo.countByEventId(eventId) >= event.getMaxParticipants())
            throw new BusinessRuleException("Event is full");
        participantRepo.save(EventParticipant.builder().eventId(eventId).userId(userId).build());
    }

    public void leaveEvent(UUID eventId, UUID userId) {
        if (!participantRepo.existsByEventIdAndUserId(eventId, userId)) throw new EntityNotFoundException("Not a participant");
        participantRepo.deleteByEventIdAndUserId(eventId, userId);
    }

    private EventResponse toResponse(Event e) {
        String name; try { name = userClient.getUserSummary(e.getOrganizerId()).getFullName(); } catch (Exception ex) { name = "Unknown"; }
        return EventResponse.builder().id(e.getId()).organizerId(e.getOrganizerId()).organizerName(name)
                .title(e.getTitle()).description(e.getDescription()).location(e.getLocation())
                .city(e.getCity()).eventDate(e.getEventDate()).category(e.getCategory())
                .maxParticipants(e.getMaxParticipants()).coverUrl(e.getCoverUrl())
                .cancelled(e.isCancelled()).participantCount(participantRepo.countByEventId(e.getId()))
                .createdAt(e.getCreatedAt()).build();
    }
}
