package com.campuslink.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

    List<ItemImage> findByItemIdOrderBySortOrderAsc(UUID itemId);
}
