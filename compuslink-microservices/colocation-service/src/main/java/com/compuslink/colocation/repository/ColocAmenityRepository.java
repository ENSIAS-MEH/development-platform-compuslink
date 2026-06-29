package com.compuslink.colocation.repository;

import com.compuslink.colocation.model.ColocAmenity;
import com.compuslink.colocation.model.ColocPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ColocAmenityRepository extends JpaRepository<ColocAmenity, UUID> {
    List<ColocAmenity> findByPost(ColocPost post);
    void deleteByPostId(UUID postId);
}
