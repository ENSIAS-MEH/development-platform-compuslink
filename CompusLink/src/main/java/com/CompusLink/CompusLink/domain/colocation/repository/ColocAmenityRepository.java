package com.CompusLink.CompusLink.domain.colocation.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocAmenity;
import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;

public interface ColocAmenityRepository extends JpaRepository<ColocAmenity, UUID> {

    // Trouver tous les équipements d'un post
    List<ColocAmenity> findByPost(ColocPost post);

    // Supprimer tous les équipements d'un post (utile pour la mise à jour ou suppression du post)
    void deleteByPost(ColocPost post);
}
