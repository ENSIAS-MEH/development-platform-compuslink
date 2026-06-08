package com.compuslink.colocation.repository;

import com.compuslink.colocation.model.ColocInterest;
import com.compuslink.colocation.model.InterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ColocInterestRepository extends JpaRepository<ColocInterest, UUID> {
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    long countByPostId(UUID postId);
    long countByPostIdAndStatus(UUID postId, InterestStatus status);
    List<ColocInterest> findByPostId(UUID postId);
    List<ColocInterest> findByUserId(UUID userId);
    void deleteByPostId(UUID postId);
}
