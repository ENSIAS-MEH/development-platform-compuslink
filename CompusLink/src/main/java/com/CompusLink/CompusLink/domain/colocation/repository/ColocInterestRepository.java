package com.CompusLink.CompusLink.domain.colocation.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CompusLink.CompusLink.domain.colocation.model.ColocInterest;
import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;

public interface ColocInterestRepository extends JpaRepository<ColocInterest, UUID> {

    // Vérifier si un utilisateur a déjà postulé
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    // Trouver tous les intérêts d'un post (pour le poster)
    List<ColocInterest> findByPostIdOrderByCreatedAtDesc(UUID postId);

    // Compter le total des intérêts
    long countByPostId(UUID postId);

    // Compter par statut (PENDING, ACCEPTED, REJECTED)
    long countByPostIdAndStatus(UUID postId, InterestStatus status);

    Optional<ColocInterest> findByPostIdAndUserId(UUID postId, UUID userId);
}
