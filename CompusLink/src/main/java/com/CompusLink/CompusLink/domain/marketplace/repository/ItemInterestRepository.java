package com.CompusLink.CompusLink.domain.marketplace.repository;

import com.CompusLink.CompusLink.domain.marketplace.model.ItemInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemInterestRepository extends JpaRepository<ItemInterest, UUID> {

    Optional<ItemInterest> findByItemIdAndUserId(UUID itemId, UUID userId);
    boolean existsByItemIdAndUserId(UUID itemId, UUID userId);
    List<ItemInterest> findByItemId(UUID itemId);
    long countByItemId(UUID itemId);
}
