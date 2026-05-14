package com.CompusLink.CompusLink.domain.colocation.repository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;
import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ColocPostRepository extends JpaRepository<ColocPost, UUID> {

    List<ColocPost> findByPosterId(UUID posterId);
    List<ColocPost> findByStatus(ColocStatus status);
    List<ColocPost> findByCityIgnoreCase(String city);
}
