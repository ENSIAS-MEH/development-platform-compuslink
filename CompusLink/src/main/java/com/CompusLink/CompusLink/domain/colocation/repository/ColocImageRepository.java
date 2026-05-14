package com.CompusLink.CompusLink.domain.colocation.repository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocImage;
import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ColocImageRepository extends JpaRepository<ColocImage, UUID> {

    List<ColocImage> findByPostOrderBySortOrderAsc(ColocPost post);
    void deleteByPost(ColocPost post);
}
