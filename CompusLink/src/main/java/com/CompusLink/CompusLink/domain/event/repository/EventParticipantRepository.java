package com.CompusLink.CompusLink.domain.event.repository;

import com.CompusLink.CompusLink.domain.event.model.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    long countByEventId(UUID eventId);
    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);
    Optional<EventParticipant> findByEventIdAndUserId(UUID eventId, UUID userId);
    List<EventParticipant> findByUserId(UUID userId);
}
