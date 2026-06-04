package com.compuslink.event.repository;
import com.compuslink.event.model.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);
    long countByEventId(UUID eventId);
    void deleteByEventIdAndUserId(UUID eventId, UUID userId);
}
