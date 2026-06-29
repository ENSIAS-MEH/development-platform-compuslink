package com.compuslink.colocation.repository;

import com.compuslink.colocation.model.ColocImage;
import com.compuslink.colocation.model.ColocPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ColocImageRepository extends JpaRepository<ColocImage, UUID> {
    @Query("SELECT i FROM ColocImage i WHERE i.post = :post ORDER BY i.isCover DESC, i.sortOrder ASC")
    List<ColocImage> findByPostOrderByPriority(ColocPost post);
    long countByPostId(UUID postId);
    void deleteByPostId(UUID postId);
}
