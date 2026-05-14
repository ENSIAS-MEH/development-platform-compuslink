package com.CompusLink.CompusLink.domain.offer.repository;

import com.CompusLink.CompusLink.domain.offer.model.Offer;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByStatus(OfferStatus status);
    List<Offer> findByPosterId(UUID posterId);
    List<Offer> findByType(OfferType type);
}
