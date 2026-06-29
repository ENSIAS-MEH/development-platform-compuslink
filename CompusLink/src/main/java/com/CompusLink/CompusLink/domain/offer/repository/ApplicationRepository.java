package com.CompusLink.CompusLink.domain.offer.repository;

import com.CompusLink.CompusLink.domain.offer.model.AppStatus;
import com.CompusLink.CompusLink.domain.offer.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findByOfferIdAndApplicantId(UUID offerId, UUID applicantId);
    List<Application> findByOfferId(UUID offerId);
    List<Application> findByApplicantId(UUID applicantId);
    boolean existsByOfferIdAndApplicantId(UUID offerId, UUID applicantId);
    long countByOfferId(UUID offerId);
    List<Application> findByOfferIdAndStatus(UUID offerId, AppStatus status);
    void deleteByOfferId(UUID offerId);
}
