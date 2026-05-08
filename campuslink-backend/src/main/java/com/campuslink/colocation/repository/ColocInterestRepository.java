package com.campuslink.colocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColocInterestRepository extends JpaRepository<ColocInterest, UUID> {

    List<ColocInterest> findByPostId(UUID postId);

    List<ColocInterest> findByUserId(UUID userId);

    Optional<ColocInterest> findByPostIdAndUserId(UUID postId, UUID userId);

    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    List<ColocInterest> findByPostIdAndStatus(UUID postId, InterestStatus status);
}
