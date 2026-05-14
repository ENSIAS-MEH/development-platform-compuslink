package com.CompusLink.CompusLink.domain.marketplace.repository;

import com.CompusLink.CompusLink.domain.marketplace.model.Item;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {

    List<Item> findBySellerId(UUID sellerId);
    List<Item> findByStatus(ItemStatus status);
    List<Item> findByCityIgnoreCase(String city);
}
