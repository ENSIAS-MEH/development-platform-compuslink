package com.campuslink.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedItemRepository extends JpaRepository<SavedItem, UUID> {

    List<SavedItem> findByUserId(UUID userId);

    List<SavedItem> findByUserIdAndTargetType(UUID userId, TargetType targetType);

    Optional<SavedItem> findByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType targetType, UUID targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType targetType, UUID targetId);

    void deleteByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType targetType, UUID targetId);
}
