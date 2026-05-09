package com.CompusLink.CompusLink.domain.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemInterestRepository extends JpaRepository<ItemInterest, UUID> {
}
