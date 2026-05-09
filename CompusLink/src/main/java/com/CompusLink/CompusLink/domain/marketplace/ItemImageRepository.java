package com.CompusLink.CompusLink.domain.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {
}
