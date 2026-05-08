package com.campuslink.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findBySellerId(UUID sellerId);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByCityAndStatus(String city, ItemStatus status);
}
