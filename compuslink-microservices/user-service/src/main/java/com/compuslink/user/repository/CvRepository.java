package com.compuslink.user.repository;

import com.compuslink.user.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CvRepository extends JpaRepository<Cv, UUID> {
    List<Cv> findByUserIdOrderByUploadedAtDesc(UUID userId);
    Optional<Cv> findByIdAndUserId(UUID id, UUID userId);
    Optional<Cv> findByUserIdAndIsDefaultTrue(UUID userId);
    long countByUserId(UUID userId);
}
