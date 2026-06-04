package com.compuslink.marketplace.repository;

import com.compuslink.marketplace.model.ItemInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemInterestRepository extends JpaRepository<ItemInterest, UUID> {
    boolean existsByItemIdAndUserId(UUID itemId, UUID userId);
    List<ItemInterest> findByItemId(UUID itemId);
    List<ItemInterest> findByUserId(UUID userId);
}
