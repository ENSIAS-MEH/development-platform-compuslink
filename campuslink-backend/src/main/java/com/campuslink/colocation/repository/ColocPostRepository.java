package com.campuslink.colocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColocPostRepository extends JpaRepository<ColocPost, UUID> {

    List<ColocPost> findByPosterId(UUID posterId);

    List<ColocPost> findByStatus(ColocStatus status);

    List<ColocPost> findByCityAndStatus(String city, ColocStatus status);
}
