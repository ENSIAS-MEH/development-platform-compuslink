package com.CompusLink.CompusLink.domain.colocation.repository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocInterest;
import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColocInterestRepository extends JpaRepository<ColocInterest, UUID> {

    Optional<ColocInterest> findByPostIdAndUserId(UUID postId, UUID userId);
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    List<ColocInterest> findByPostId(UUID postId);
    long countByPostIdAndStatus(UUID postId, InterestStatus status);
}
