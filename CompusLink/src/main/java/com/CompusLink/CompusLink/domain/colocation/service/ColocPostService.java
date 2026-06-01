package com.CompusLink.CompusLink.domain.colocation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CompusLink.CompusLink.domain.colocation.dto.ColocAmenityDTO;
import com.CompusLink.CompusLink.domain.colocation.dto.ColocImageDTO;
import com.CompusLink.CompusLink.domain.colocation.dto.ColocPostDTO;
import com.CompusLink.CompusLink.domain.colocation.model.ColocAmenity;
import com.CompusLink.CompusLink.domain.colocation.model.ColocImage;
import com.CompusLink.CompusLink.domain.colocation.model.ColocInterest;
import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;
import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import com.CompusLink.CompusLink.domain.colocation.model.HousingType;
import com.CompusLink.CompusLink.domain.colocation.model.InterestStatus;
import com.CompusLink.CompusLink.domain.colocation.repository.ColocAmenityRepository;
import com.CompusLink.CompusLink.domain.colocation.repository.ColocImageRepository;
import com.CompusLink.CompusLink.domain.colocation.repository.ColocInterestRepository;
import com.CompusLink.CompusLink.domain.colocation.repository.ColocPostRepository;
import com.CompusLink.CompusLink.exception.AccessDeniedException;
import com.CompusLink.CompusLink.exception.BusinessRuleException;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ColocPostService {

    private final ColocPostRepository postRepository;
    private final ColocImageRepository imageRepository;
    private final ColocInterestRepository interestRepository;
    private final ColocAmenityRepository amenityRepository;
    private final ColocFileStorageService fileStorageService;

    // --- Task 5.3: Publier un post (Optimisé) ---
    public ColocPostDTO createPost(ColocPostDTO request, UUID posterId) {
        ColocPost post = ColocPost.builder()
                .posterId(posterId)
                .title(request.getTitle())
                .description(request.getDescription())
                .city(request.getCity())
                .address(request.getAddress())
                .startDate(request.getStartDate())
                .spotsNeeded(request.getSpotsNeeded())
                .spotsConfirmed(0)
                .housingType(request.getHousingType())
                .rentPerPerson(request.getRentPerPerson())
                .furnished(request.getFurnished())
                .status(ColocStatus.OPEN)
                .build();

        ColocPost saved = postRepository.save(post);

        List<ColocAmenityDTO> savedAmenities = new ArrayList<>();
        // Équipements optionnels
        if (request.getAmenities() != null) {
            request.getAmenities().forEach(amenityDto -> {
                ColocAmenity amenity = amenityRepository.save(ColocAmenity.builder()
                        .post(saved)
                        .amenityType(amenityDto.amenityType())
                        .build());
                savedAmenities.add(new ColocAmenityDTO(amenity.getId(), amenity.getAmenityType()));
            });
        }

        // On évite getPostDetails immédiat pour des raisons de performance et de cycle de vie de transaction
        return new ColocPostDTO(
                saved.getId(),
                saved.getPosterId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getCity(),
                saved.getAddress(),
                saved.getStartDate(),
                saved.getSpotsNeeded(),
                saved.getSpotsConfirmed(),
                saved.getHousingType(),
                saved.getRentPerPerson(),
                saved.getFurnished(),
                saved.getStatus(),
                null, // Pas encore de cover
                0L, // 0 intérêt total
                0L, // 0 intérêt en attente
                savedAmenities,
                List.of(),
                saved.getCreatedAt()
        );
    }

    public List<ColocPostDTO> getMyPosts(UUID posterId) {
        return postRepository.findByPosterId(posterId).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // --- Task 5.3: Lister les posts (Filtrage & Pagination) ---
    public Page<ColocPostDTO> browsePosts(String city, HousingType type, Boolean furnished, BigDecimal rentMax, ColocStatus status, Pageable pageable) {        return postRepository.findWithFilters(city, type, furnished, rentMax, status, pageable)
                .map(this::toSummaryDTO);
    }

    // --- Task 5.3: Voir le détail (Sécurisé contre les NullPointer) ---
    public ColocPostDTO getPostDetails(UUID id, UUID currentUserId) {
        ColocPost post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

        long totalInterests = interestRepository.countByPostId(id);
        
        // CORRECTION : Utilisation de Objects.equals pour supporter le mode déconnecté (currentUserId == null)
        long pendingInterests = java.util.Objects.equals(post.getPosterId(), currentUserId)
                ? interestRepository.countByPostIdAndStatus(id, InterestStatus.PENDING) 
                : -1;

        return toFullDTO(post, totalInterests, (int) pendingInterests);
    }

    // --- Task 5.3: Gestion des places (Logique FULL/OPEN automatique) ---
    public void updateSpotsConfirmed(UUID postId, int count, UUID currentUserId) {
        ColocPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
        
        if (!java.util.Objects.equals(post.getPosterId(), currentUserId)) {
            throw new AccessDeniedException("Accès refusé : Vous n'êtes pas l'auteur de cette annonce");
        }
        if (count < 0 || count > post.getSpotsNeeded()) {
            throw new BusinessRuleException("Nombre de places spécifié invalide");
        }

        post.setSpotsConfirmed(count);

        if (count >= post.getSpotsNeeded()) {
            post.setStatus(ColocStatus.FULL);
        } else {
            post.setStatus(ColocStatus.OPEN);
        }
        
        postRepository.save(post);
    }

    // --- Task 5.3: Intérêts (Acceptation avec atomicité préservée) ---
    public void handleInterestStatus(UUID interestId, InterestStatus newStatus, UUID currentUserId) {
        ColocInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new EntityNotFoundException("Demande d'intérêt non trouvée"));
        ColocPost post = postRepository.findById(interest.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post associé non trouvé"));

        if (!java.util.Objects.equals(post.getPosterId(), currentUserId)) {
            throw new AccessDeniedException("Non autorisé à modifier cette demande");
        }
        if (newStatus == InterestStatus.PENDING) {
            throw new BusinessRuleException("Retour au statut PENDING impossible");
        }

        interest.setStatus(newStatus);
        interestRepository.save(interest);

        if (newStatus == InterestStatus.ACCEPTED) {
            // Modification directe pour éviter un findById redondant
            int currentConfirmed = post.getSpotsConfirmed();
            if (currentConfirmed < post.getSpotsNeeded()) {
                post.setSpotsConfirmed(currentConfirmed + 1);
                if (post.getSpotsConfirmed() >= post.getSpotsNeeded()) {
                    post.setStatus(ColocStatus.FULL);
                }
                postRepository.save(post);
            } else {
                throw new BusinessRuleException("Toutes les places de cette colocation ont déjà été confirmées.");
            }
        }
    }

    // --- Task 5.3: Gestion des photos ---
    public void uploadPhoto(UUID postId, MultipartFile file, boolean isCover, UUID requesterId) {
        ColocPost post = postRepository.findById(postId).orElseThrow();
        if (!java.util.Objects.equals(post.getPosterId(), requesterId))
            throw new AccessDeniedException("Non autorisé");
        
        if (imageRepository.countByPostId(postId) >= 10)
            throw new BusinessRuleException("Limite de 10 photos atteinte");

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

    // --- Task 5.3: Exprimer son intérêt ---
    public void expressInterest(UUID postId, String message, UUID userId) {
        ColocPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

        if (post.getStatus() != ColocStatus.OPEN) {
            throw new BusinessRuleException("L'annonce n'est plus ouverte aux candidatures");
        }
        if (java.util.Objects.equals(post.getPosterId(), userId)) {
            throw new BusinessRuleException("Vous ne pouvez pas postuler à votre propre annonce");
        }
        if (interestRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new DuplicateResourceException("Vous avez déjà exprimé votre intérêt pour ce logement");
        }

        ColocInterest interest = ColocInterest.builder()
                .postId(postId)
                .userId(userId)
                .message(message)
                .status(InterestStatus.PENDING)
                .build();

        interestRepository.save(interest);
    }

    // --- Mappers Internes ---
    private ColocPostDTO toFullDTO(ColocPost post, long total, int pending) {
        List<ColocAmenityDTO> amenities = amenityRepository.findByPost(post).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType()))
                .collect(Collectors.toList());

        List<ColocImageDTO> images = imageRepository.findByPostOrderByPriority(post).stream()
                .map(i -> new ColocImageDTO(i.getId(), i.getUrl(), i.getSortOrder(), i.getIsCover()))
                .toList();

        String cover = imageRepository.findByPostAndIsCoverTrue(post)
                .map(ColocImage::getUrl)
                .orElse(null);

        return new ColocPostDTO(
                post.getId(),
                post.getPosterId(),
                post.getTitle(),
                post.getDescription(),
                post.getCity(),
                post.getAddress(),
                post.getStartDate(),
                post.getSpotsNeeded(),
                post.getSpotsConfirmed(),
                post.getHousingType(),
                post.getRentPerPerson(),
                post.getFurnished(),
                post.getStatus(),
                cover,
                total,
                (long) pending,
                amenities,
                images,
                post.getCreatedAt()
        );
    }

    private ColocPostDTO toSummaryDTO(ColocPost post) {
        String cover = imageRepository.findByPostAndIsCoverTrue(post)
                .map(ColocImage::getUrl)
                .orElse(null);

        List<ColocAmenityDTO> amenities = amenityRepository.findByPost(post).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType()))
                .collect(Collectors.toList());

        return new ColocPostDTO(
                post.getId(),
                post.getPosterId(),
                post.getTitle(),
                null,
                post.getCity(),
                null,
                null,
                post.getSpotsNeeded(),
                post.getSpotsConfirmed(),
                post.getHousingType(),
                post.getRentPerPerson(),
                post.getFurnished(),
                post.getStatus(),
                cover,
                null,
                null,
                amenities,
                null,
                post.getCreatedAt()
        );
    }
}