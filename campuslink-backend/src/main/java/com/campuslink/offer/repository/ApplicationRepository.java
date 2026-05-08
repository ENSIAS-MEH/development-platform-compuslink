package com.campuslink.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByOfferId(UUID offerId);

    List<Application> findByApplicantId(UUID applicantId);

    Optional<Application> findByOfferIdAndApplicantId(UUID offerId, UUID applicantId);

    boolean existsByOfferIdAndApplicantId(UUID offerId, UUID applicantId);

    List<Application> findByOfferIdAndStatus(UUID offerId, AppStatus status);
}
