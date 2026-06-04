package com.compuslink.offer.repository;

import com.compuslink.offer.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByOfferId(UUID offerId);
    List<Application> findByApplicantId(UUID applicantId);
    boolean existsByOfferIdAndApplicantId(UUID offerId, UUID applicantId);
}
