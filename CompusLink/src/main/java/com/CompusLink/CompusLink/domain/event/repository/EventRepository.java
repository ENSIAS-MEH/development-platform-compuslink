package com.CompusLink.CompusLink.domain.event.repository;

import com.CompusLink.CompusLink.domain.event.model.Event;
import com.CompusLink.CompusLink.domain.event.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizerId(UUID organizerId);
    List<Event> findByCategoryAndCancelledFalse(EventCategory category);
    List<Event> findByCancelledFalseOrderByEventDateAsc();
    List<Event> findByCityIgnoreCaseAndCancelledFalse(String city);
}
