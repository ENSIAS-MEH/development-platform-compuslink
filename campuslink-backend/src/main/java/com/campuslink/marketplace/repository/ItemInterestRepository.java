package com.campuslink.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemInterestRepository extends JpaRepository<ItemInterest, UUID> {

    List<ItemInterest> findByItemId(UUID itemId);

    List<ItemInterest> findByUserId(UUID userId);

    Optional<ItemInterest> findByItemIdAndUserId(UUID itemId, UUID userId);

    boolean existsByItemIdAndUserId(UUID itemId, UUID userId);
}
