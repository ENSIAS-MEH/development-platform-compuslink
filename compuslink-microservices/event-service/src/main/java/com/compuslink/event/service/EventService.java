package com.compuslink.event.service;

import com.compuslink.common.exception.*;
import com.compuslink.event.dto.*;
import com.compuslink.event.model.*;
import com.compuslink.event.repository.EventParticipantRepository;
import com.compuslink.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final EventFileStorageService fileStorage;

    public EventResponse setCover(UUID eventId, org.springframework.web.multipart.MultipartFile file, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getOrganizerId().equals(userId))
            throw new AccessDeniedException("Non autorisé");
        event.setCoverUrl(fileStorage.store(file));
        return toResponse(eventRepository.save(event), userId);
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
        if (category != null) events = eventRepository.findByCategoryAndCancelledFalse(category);
        else if (city != null) events = eventRepository.findByCityIgnoreCaseAndCancelledFalse(city);
        else events = eventRepository.findByCancelledFalseOrderByEventDateAsc();
        return events.stream().map(e -> toResponse(e, currentUserId)).collect(Collectors.toList());
    }

    public EventResponse getEvent(UUID id, UUID currentUserId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        return toResponse(event, currentUserId);
    }

    public EventResponse join(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (event.isCancelled()) throw new BusinessRuleException("Cet événement est annulé");
        if (event.getEventDate().isBefore(OffsetDateTime.now()))
            throw new BusinessRuleException("Cet événement est déjà terminé");
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

    public EventResponse cancelEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getOrganizerId().equals(userId))
            throw new AccessDeniedException("Non autorisé");
        if (event.isCancelled())
            throw new BusinessRuleException("Cet événement est déjà annulé");
        event.setCancelled(true);
        return toResponse(eventRepository.save(event), userId);
    }

    public void deleteEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getOrganizerId().equals(userId))
            throw new AccessDeniedException("Non autorisé");
        eventRepository.delete(event);
    }

    public List<EventResponse> getMyEvents(UUID userId) {
        return eventRepository.findByOrganizerId(userId).stream()
                .map(e -> toResponse(e, userId)).collect(Collectors.toList());
    }

    public List<EventResponse> getMyParticipations(UUID userId) {
        return participantRepository.findByUserId(userId).stream()
                .map(p -> eventRepository.findById(p.getEventId()))
                .filter(Optional::isPresent)
                .map(opt -> toResponse(opt.get(), userId))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getParticipants(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getOrganizerId().equals(userId))
            throw new AccessDeniedException("Non autorisé");
        return participantRepository.findByEventId(eventId).stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("userId", p.getUserId());
                    map.put("joinedAt", p.getJoinedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private EventResponse toResponse(Event event, UUID currentUserId) {
        long count = participantRepository.countByEventId(event.getId());
        boolean participating = currentUserId != null
                && participantRepository.existsByEventIdAndUserId(event.getId(), currentUserId);
        return EventResponse.builder()
                .id(event.getId()).organizerId(event.getOrganizerId())
                .title(event.getTitle()).description(event.getDescription())
                .location(event.getLocation()).city(event.getCity())
                .eventDate(event.getEventDate()).category(event.getCategory())
                .maxParticipants(event.getMaxParticipants())
                .participantCount(count).isParticipating(participating)
                .coverUrl(event.getCoverUrl()).cancelled(event.isCancelled())
                .createdAt(event.getCreatedAt()).build();
    }
}
