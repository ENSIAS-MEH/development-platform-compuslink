package com.campuslink.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByPosterId(UUID posterId);

    List<Offer> findByStatus(OfferStatus status);

    List<Offer> findByType(OfferType type);

    List<Offer> findByTypeAndStatus(OfferType type, OfferStatus status);
}
