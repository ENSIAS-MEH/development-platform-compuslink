package com.compuslink.marketplace.repository;

import com.compuslink.marketplace.model.Item;
import com.compuslink.marketplace.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {
    List<ItemImage> findByItemOrderBySortOrderAsc(Item item);
    Optional<ItemImage> findFirstByItemAndIsCoverTrue(Item item);
}
