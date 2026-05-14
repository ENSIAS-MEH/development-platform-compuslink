package com.CompusLink.CompusLink.domain.colocation.repository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocAmenity;
import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ColocAmenityRepository extends JpaRepository<ColocAmenity, UUID> {

    List<ColocAmenity> findByPost(ColocPost post);
    void deleteByPost(ColocPost post);
}
