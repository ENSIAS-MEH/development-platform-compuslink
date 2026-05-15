package com.CompusLink.CompusLink.domain.colocation.service;

import com.CompusLink.CompusLink.domain.colocation.dto.*;
import com.CompusLink.CompusLink.domain.colocation.model.*;
import com.CompusLink.CompusLink.domain.colocation.repository.*;
import com.CompusLink.CompusLink.exception.*; // Task 1.2
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ColocPostService {

    private final ColocPostRepository postRepository;
    private final ColocImageRepository imageRepository;
    private final ColocInterestRepository interestRepository;
    private final ColocAmenityRepository amenityRepository;
    private final ColocFileStorageService fileStorageService;

    // --- Task 5.3: Publier un post ---
    public ColocPostDTO createPost(ColocPostDTO request, UUID posterId) {
        ColocPost post = ColocPost.builder()
                .posterId(posterId)
                .title(request.title())
                .description(request.description())
                .city(request.city())
                .address(request.address())
                .startDate(request.startDate())
                .spotsNeeded(request.spotsNeeded())
                .spotsConfirmed(0)
                .housingType(request.housingType())
                .rentPerPerson(request.rentPerPerson())
                .furnished(request.furnished())
                .status(ColocStatus.OPEN)
                .build();

        ColocPost saved = postRepository.save(post);

        // Équipements optionnels
        if (request.amenities() != null) {
            request.amenities().forEach(amenityDto -> 
                amenityRepository.save(ColocAmenity.builder()
                    .post(saved)
                    .amenityType(amenityDto.amenityType())
                    .build())
            );
        }

        return getPostDetails(saved.getId(), posterId);
    }

    // --- Task 5.3: Lister les posts (Filtrage & Pagination) ---
    public Page<ColocPostDTO> browsePosts(String city, HousingType type, Boolean furnished, BigDecimal rentMax, ColocStatus status, Pageable pageable) {
        return postRepository.findWithFilters(city, type, furnished, rentMax, status, pageable)
                .map(this::toSummaryDTO);
    }

    // --- Task 5.3: Voir le détail ---
    public ColocPostDTO getPostDetails(UUID id, UUID currentUserId) {
        ColocPost post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

        long totalInterests = interestRepository.countByPostId(id);
        
        // Règle métier : -1 pour les non-posteurs, sinon le compte réel
        long pendingInterests = post.getPosterId().equals(currentUserId) 
                ? interestRepository.countByPostIdAndStatus(id, InterestStatus.PENDING) 
                : -1;

        return toFullDTO(post, totalInterests, (int)pendingInterests);
    }

    // --- Task 5.3: Gestion des places (Logique FULL/OPEN automatique) ---
    public void updateSpotsConfirmed(UUID postId, int count, UUID currentUserId) {
        ColocPost post = postRepository.findById(postId).orElseThrow();
        
        if (!post.getPosterId().equals(currentUserId)) throw new AccessDeniedException("Accès refusé");
        if (count < 0 || count > post.getSpotsNeeded()) throw new BusinessRuleException("Nombre de places invalide");

        post.setSpotsConfirmed(count);

        if (count >= post.getSpotsNeeded()) {
            post.setStatus(ColocStatus.FULL);
        } else if (post.getStatus() == ColocStatus.FULL) {
            post.setStatus(ColocStatus.OPEN);
        }
        
        postRepository.save(post);
    }

    // --- Task 5.3: Intérêts (Acceptation avec atomicité) ---
    public void handleInterestStatus(UUID interestId, InterestStatus newStatus, UUID currentUserId) {
        ColocInterest interest = interestRepository.findById(interestId).orElseThrow();
        ColocPost post = postRepository.findById(interest.getPostId()).orElseThrow();

        if (!post.getPosterId().equals(currentUserId)) throw new AccessDeniedException("Non autorisé");
        if (newStatus == InterestStatus.PENDING) throw new BusinessRuleException("Retour à PENDING impossible");

        interest.setStatus(newStatus);
        interestRepository.save(interest);

        if (newStatus == InterestStatus.ACCEPTED) {
            updateSpotsConfirmed(post.getId(), post.getSpotsConfirmed() + 1, currentUserId);
        }
    }

    // --- Task 5.3: Gestion des photos ---
    public void uploadPhoto(UUID postId, MultipartFile file, boolean isCover, UUID requesterId) {
        ColocPost post = postRepository.findById(postId).orElseThrow();
        if (!post.getPosterId().equals(requesterId)) throw new AccessDeniedException("Non autorisé");
        
        if (imageRepository.countByPostId(postId) >= 10) throw new BusinessRuleException("Limite de 10 photos");

        String url = fileStorageService.store(file);
        
        if (isCover) { // Réinitialiser l'ancienne cover
            imageRepository.findByPostAndIsCoverTrue(post).ifPresent(img -> {
                img.setIsCover(false);
                imageRepository.save(img);
            });
        }

        imageRepository.save(ColocImage.builder()
                .post(post)
                .url(url)
                .isCover(isCover)
                .build());
    }

    // --- Mappers Internes ---
    private ColocPostDTO toFullDTO(ColocPost post, long total, int pending) {
        return ColocPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .city(post.getCity())
                .spotsNeeded(post.getSpotsNeeded())
                .spotsConfirmed(post.getSpotsConfirmed())
                .status(post.getStatus())
                .pendingInterests((long)pending)
                .totalInterests(total)
                .images(imageRepository.findByPostOrderByPriority(post).stream()
                        .map(i -> new ColocImageDTO(i.getId(), i.getUrl(), i.getSortOrder(), i.getIsCover()))
                        .toList())
                .build();
    }

    private ColocPostDTO toSummaryDTO(ColocPost post) {
    // Récupération de l'URL de la photo de couverture
    String cover = imageRepository.findByPostAndIsCoverTrue(post)
            .map(ColocImage::getUrl)
            .orElse(null);

    // Récupération de la liste des types d'équipements pour le résumé
    List<ColocAmenityDTO> amenities = amenityRepository.findByPost(post).stream()
            .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType()))
            .collect(Collectors.toList());

    // Construction du DTO avec tous les champs requis par la Task 5.1
    return ColocPostDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .city(post.getCity())
            .housingType(post.getHousingType()) // Ajouté pour le résumé
            .rentPerPerson(post.getRentPerPerson()) // Ajouté pour le résumé
            .furnished(post.getFurnished()) // Ajouté pour le résumé
            .spotsNeeded(post.getSpotsNeeded()) // Places cherchées
            .spotsConfirmed(post.getSpotsConfirmed()) // Places confirmées
            .status(post.getStatus())
            .coverUrl(cover) // URL de la cover
            .amenities(amenities) // Liste des équipements
            .createdAt(post.getCreatedAt()) // Date de création pour le tri
            .build();
}

        // À ajouter dans ColocPostService.java
@Transactional
public void expressInterest(UUID postId, String message, UUID userId) {
    // 1. Vérifier que le post existe
    ColocPost post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

    // 2. Vérifier les règles métier (Task 5.3)
    if (post.getStatus() != ColocStatus.OPEN) {
        throw new BusinessRuleException("L'annonce n'est plus ouverte");
    }
    if (post.getPosterId().equals(userId)) {
        throw new BusinessRuleException("Vous ne pouvez pas postuler à votre propre annonce");
    }
    if (interestRepository.existsByPostIdAndUserId(postId, userId)) {
        throw new DuplicateResourceException("Vous avez déjà exprimé votre intérêt pour ce post");
    }

    // 3. Créer et sauvegarder l'intérêt
    ColocInterest interest = ColocInterest.builder()
            .postId(postId)
            .userId(userId)
            .message(message)
            .status(InterestStatus.PENDING)
            .build();
            
    interestRepository.save(interest);
}
}