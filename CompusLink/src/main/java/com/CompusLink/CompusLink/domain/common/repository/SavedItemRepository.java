package com.CompusLink.CompusLink.domain.common.repository;

import com.CompusLink.CompusLink.domain.common.model.SavedItem;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedItemRepository extends JpaRepository<SavedItem, UUID> {

    List<SavedItem> findByUserId(UUID userId);
    List<SavedItem> findByUserIdAndTargetType(UUID userId, TargetType targetType);
    Optional<SavedItem> findByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType targetType, UUID targetId);
    boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType targetType, UUID targetId);
}
