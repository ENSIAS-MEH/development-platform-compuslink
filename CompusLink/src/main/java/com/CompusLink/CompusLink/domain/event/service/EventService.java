package com.CompusLink.CompusLink.domain.event.service;

import com.CompusLink.CompusLink.domain.event.dto.CreateEventRequest;
import com.CompusLink.CompusLink.domain.event.dto.EventResponse;
import com.CompusLink.CompusLink.domain.event.model.Event;
import com.CompusLink.CompusLink.domain.event.model.EventCategory;
import com.CompusLink.CompusLink.domain.event.model.EventParticipant;
import com.CompusLink.CompusLink.domain.event.repository.EventParticipantRepository;
import com.CompusLink.CompusLink.domain.event.repository.EventRepository;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.exception.BusinessRuleException;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public void deleteEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        if (!event.getOrganizerId().equals(userId))
            throw new com.CompusLink.CompusLink.exception.AccessDeniedException("Non autorisé");
        eventRepository.delete(event);
    }

    public EventResponse cancelEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        if (!event.getOrganizerId().equals(userId))
            throw new com.CompusLink.CompusLink.exception.AccessDeniedException("Non autorisé");
        if (event.isCancelled())
            throw new BusinessRuleException("Cet événement est déjà annulé");
        event.setCancelled(true);
        return toResponse(eventRepository.save(event), userId);
    }

    public List<EventResponse> getMyParticipations(UUID userId) {
        return participantRepository.findByUserId(userId).stream()
                .map(p -> eventRepository.findById(p.getEventId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> toResponse(opt.get(), userId))
                .collect(Collectors.toList());
    }

    public void updateCoverUrl(UUID eventId, String url, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        if (!event.getOrganizerId().equals(userId))
            throw new com.CompusLink.CompusLink.exception.AccessDeniedException("Non autorisé");
        event.setCoverUrl(url);
        eventRepository.save(event);
    }

    public EventResponse createEvent(CreateEventRequest request, UUID organizerId) {
        Event event = Event.builder()
                .organizerId(organizerId)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .city(request.getCity())
                .eventDate(request.getEventDate())
                .category(request.getCategory())
                .maxParticipants(request.getMaxParticipants())
                .coverUrl(request.getCoverUrl())
                .build();
        return toResponse(eventRepository.save(event), organizerId);
    }

    public List<EventResponse> browseEvents(EventCategory category, String city, UUID currentUserId) {
        List<Event> events;
        if (category != null) {
            events = eventRepository.findByCategoryAndCancelledFalse(category);
        } else if (city != null) {
            events = eventRepository.findByCityIgnoreCaseAndCancelledFalse(city);
        } else {
            events = eventRepository.findByCancelledFalseOrderByEventDateAsc();
        }
        return events.stream().map(e -> toResponse(e, currentUserId)).collect(Collectors.toList());
    }

    public EventResponse getEvent(UUID id, UUID currentUserId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event", id));
        return toResponse(event, currentUserId);
    }

    public EventResponse join(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        if (event.isCancelled()) throw new BusinessRuleException("Cet événement est annulé");
        if (participantRepository.existsByEventIdAndUserId(eventId, userId))
            throw new DuplicateResourceException("Vous participez déjà à cet événement");
        long count = participantRepository.countByEventId(eventId);
        if (event.getMaxParticipants() != null && count >= event.getMaxParticipants())
            throw new BusinessRuleException("L'événement est complet");
        participantRepository.save(EventParticipant.builder().eventId(eventId).userId(userId).build());
        return toResponse(event, userId);
    }

    public EventResponse leave(UUID eventId, UUID userId) {
        EventParticipant p = participantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Participation non trouvée"));
        participantRepository.delete(p);
        Event event = eventRepository.findById(eventId).orElseThrow();
        return toResponse(event, userId);
    }

    public List<EventResponse> getMyEvents(UUID userId) {
        return eventRepository.findByOrganizerId(userId).stream()
                .map(e -> toResponse(e, userId))
                .collect(Collectors.toList());
    }

    public java.util.List<java.util.Map<String, Object>> getParticipants(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        if (!event.getOrganizerId().equals(userId))
            throw new com.CompusLink.CompusLink.exception.AccessDeniedException("Non autorisé");
        return participantRepository.findByEventId(eventId).stream()
                .map(p -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", p.getId());
                    map.put("joinedAt", p.getJoinedAt());
                    userRepository.findById(p.getUserId()).ifPresent(u -> {
                        map.put("userId", u.getId());
                        map.put("fullName", u.getFullName());
                        map.put("email", u.getEmail());
                        map.put("profilePicUrl", u.getProfilePicUrl());
                    });
                    return map;
                })
                .collect(Collectors.toList());
    }

    private EventResponse toResponse(Event event, UUID currentUserId) {
        long count = participantRepository.countByEventId(event.getId());
        boolean participating = currentUserId != null && participantRepository.existsByEventIdAndUserId(event.getId(), currentUserId);
        String organizerName = userRepository.findById(event.getOrganizerId())
                .map(u -> u.getFullName() != null ? u.getFullName() : u.getEmail())
                .orElse("Unknown");
        return EventResponse.builder()
                .id(event.getId())
                .organizerId(event.getOrganizerId())
                .organizerName(organizerName)
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .city(event.getCity())
                .eventDate(event.getEventDate())
                .category(event.getCategory())
                .maxParticipants(event.getMaxParticipants())
                .participantCount(count)
                .isParticipating(participating)
                .coverUrl(event.getCoverUrl())
                .cancelled(event.isCancelled())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
