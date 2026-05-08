package com.campuslink.colocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColocImageRepository extends JpaRepository<ColocImage, UUID> {

    List<ColocImage> findByPostIdOrderBySortOrderAsc(UUID postId);
}
