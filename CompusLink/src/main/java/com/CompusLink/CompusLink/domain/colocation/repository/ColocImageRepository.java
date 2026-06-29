package com.CompusLink.CompusLink.domain.colocation.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CompusLink.CompusLink.domain.colocation.model.ColocImage;
import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;

public interface ColocImageRepository extends JpaRepository<ColocImage, UUID> {

    // Images triées : cover en premier (isCover DESC), puis par sortOrder
    @Query("SELECT i FROM ColocImage i WHERE i.post = :post ORDER BY i.isCover DESC, i.sortOrder ASC")
    List<ColocImage> findByPostOrderByPriority(@Param("post") ColocPost post);

    // Uniquement la cover d'un post
    Optional<ColocImage> findByPostAndIsCoverTrue(ColocPost post);

    // Suppression de toutes les images d'un post
    void deleteByPost(ColocPost post);

    // Compter les images pour la limite de 10
    long countByPostId(UUID postId);
}
