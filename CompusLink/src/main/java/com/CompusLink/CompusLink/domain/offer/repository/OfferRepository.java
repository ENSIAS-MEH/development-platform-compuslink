package com.CompusLink.CompusLink.domain.offer.repository;

import com.CompusLink.CompusLink.domain.offer.model.LocationType;
import com.CompusLink.CompusLink.domain.offer.model.Offer;
import com.CompusLink.CompusLink.domain.offer.model.OfferStatus;
import com.CompusLink.CompusLink.domain.offer.model.OfferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByStatus(OfferStatus status);
    List<Offer> findByPosterId(UUID posterId);
    List<Offer> findByType(OfferType type);

    @Query("SELECT o FROM Offer o WHERE "
            + "(:type IS NULL OR o.type = :type) AND "
            + "(:city IS NULL OR o.city = :city) AND "
            + "(:domain IS NULL OR o.domain = :domain) AND "
            + "(:locationType IS NULL OR o.locationType = :locationType) AND "
            + "(:status IS NULL OR o.status = :status)")
    Page<Offer> findFiltered(
            @Param("type") OfferType type,
            @Param("city") String city,
            @Param("domain") String domain,
            @Param("locationType") LocationType locationType,
            @Param("status") OfferStatus status,
            Pageable pageable);
}
