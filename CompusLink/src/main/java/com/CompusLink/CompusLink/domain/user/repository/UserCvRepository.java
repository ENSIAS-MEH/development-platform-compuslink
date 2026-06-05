package com.CompusLink.CompusLink.domain.user.repository;

import com.CompusLink.CompusLink.domain.user.model.UserCv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserCvRepository extends JpaRepository<UserCv, UUID> {
    List<UserCv> findByUserIdOrderByUploadedAtDesc(UUID userId);
}
