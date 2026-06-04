package com.compuslink.offer.repository;

import com.compuslink.offer.model.Offer;
import com.compuslink.offer.model.OfferStatus;
import com.compuslink.offer.model.OfferType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {
    List<Offer> findByPosterId(UUID posterId);
    List<Offer> findByStatus(OfferStatus status);
    List<Offer> findByTypeAndStatus(OfferType type, OfferStatus status);
}
