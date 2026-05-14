package com.CompusLink.CompusLink.domain.marketplace.repository;

import com.CompusLink.CompusLink.domain.marketplace.model.Item;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

    List<ItemImage> findByItemOrderBySortOrderAsc(Item item);
    void deleteByItem(Item item);
}
