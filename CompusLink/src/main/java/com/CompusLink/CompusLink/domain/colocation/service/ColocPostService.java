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
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    // --- Publier un post ---
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
        if (request.getAmenities() != null) {
            request.getAmenities().forEach(amenityDto -> {
                ColocAmenity amenity = amenityRepository.save(ColocAmenity.builder()
                        .post(saved)
                        .amenityType(amenityDto.amenityType())
                        .build());
                savedAmenities.add(new ColocAmenityDTO(amenity.getId(), amenity.getAmenityType()));
            });
        }

        String authorName = userRepository.findById(posterId)
                .map(u -> u.getFullName())
                .orElse("Étudiant Anonyme");

        return ColocPostDTO.builder()
                .id(saved.getId())
                .posterId(saved.getPosterId())
                .posterName(authorName)
                .title(saved.getTitle())
                .description(saved.getDescription())
                .city(saved.getCity())
                .address(saved.getAddress())
                .startDate(saved.getStartDate())
                .spotsNeeded(saved.getSpotsNeeded())
                .spotsConfirmed(saved.getSpotsConfirmed())
                .housingType(saved.getHousingType())
                .rentPerPerson(saved.getRentPerPerson())
                .furnished(saved.getFurnished())
                .status(saved.getStatus())
                .coverUrl(null)
                .totalInterests(0L)
                .pendingInterests(0L)
                .amenities(savedAmenities)
                .images(List.of())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // --- Parcourir les posts ---
    public Page<ColocPostDTO> browsePosts(UUID currentUserId, String city, HousingType type, Boolean furnished, Integer spotsNeeded, BigDecimal rentMax, Pageable pageable) {
        return postRepository.findWithFilters(currentUserId, city, type, furnished, spotsNeeded, rentMax, pageable)
                .map(this::toSummaryDTO);
    }

    // --- Mes Publications ---
    public List<ColocPostDTO> getMyPosts(UUID posterId) {
        return postRepository.findByPosterId(posterId).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // --- Supprimer un post ---
    public void deletePost(UUID id, UUID currentUserId) {
        ColocPost post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
        if (!java.util.Objects.equals(post.getPosterId(), currentUserId)) {
            throw new AccessDeniedException("Non autorisé à supprimer ce post");
        }
        postRepository.delete(post);
    }

    // --- Voir le détail ---
    public ColocPostDTO getPostDetails(UUID id, UUID currentUserId) {
        ColocPost post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

        long totalInterests = interestRepository.countByPostId(id);

        long pendingInterests = java.util.Objects.equals(post.getPosterId(), currentUserId)
                ? interestRepository.countByPostIdAndStatus(id, InterestStatus.PENDING) 
                : -1;

        return toFullDTO(post, totalInterests, (int) pendingInterests);
    }

    // --- Gestion des places ---
    public void updateSpotsConfirmed(UUID postId, int count, UUID currentUserId) {
        ColocPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
        
        if (!java.util.Objects.equals(post.getPosterId(), currentUserId)) {
            throw new AccessDeniedException("Accès refusé");
        }
        if (count < 0 || count > post.getSpotsNeeded()) {
            throw new BusinessRuleException("Nombre de places spécifié invalide");
        }

        post.setSpotsConfirmed(count);
        post.setStatus(count >= post.getSpotsNeeded() ? ColocStatus.FULL : ColocStatus.OPEN);
        
        postRepository.save(post);
    }

    // --- Gestion des demandes d'intérêt ---
    public void handleInterestStatus(UUID interestId, InterestStatus newStatus, UUID currentUserId) {
        ColocInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée"));
        ColocPost post = postRepository.findById(interest.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post associé non trouvé"));

        if (!java.util.Objects.equals(post.getPosterId(), currentUserId)) {
            throw new AccessDeniedException("Non autorisé");
        }
        if (newStatus == InterestStatus.PENDING) {
            throw new BusinessRuleException("Action impossible");
        }

        interest.setStatus(newStatus);
        interestRepository.save(interest);

        if (newStatus == InterestStatus.ACCEPTED) {
            int currentConfirmed = post.getSpotsConfirmed();
            if (currentConfirmed < post.getSpotsNeeded()) {
                post.setSpotsConfirmed(currentConfirmed + 1);
                if (post.getSpotsConfirmed() >= post.getSpotsNeeded()) {
                    post.setStatus(ColocStatus.FULL);
                }
                postRepository.save(post);
            } else {
                throw new BusinessRuleException("Toutes les places ont déjà été confirmées.");
            }
        }
    }

    // --- Multi-Upload de photos avec liaison de la couverture ---
    public void uploadPhotos(UUID postId, List<MultipartFile> files, UUID requesterId) {
        ColocPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
        if (!java.util.Objects.equals(post.getPosterId(), requesterId)) {
            throw new AccessDeniedException("Non autorisé");
        }
        
        long currentCount = imageRepository.countByPostId(postId);
        if (currentCount + files.size() > 10) {
            throw new BusinessRuleException("Limite globale de 10 photos atteinte.");
        }

        String firstUploadedUrl = null;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String url = fileStorageService.store(file);

            boolean isCover = (currentCount == 0 && i == 0);
            if (isCover) {
                firstUploadedUrl = url;
            }

            imageRepository.save(ColocImage.builder()
                    .post(post)
                    .url(url)
                    .isCover(isCover)
                    .sortOrder((int) (currentCount + i))
                    .build());
        }

        // Utilisation du setter de coverUrl (généré par @Data sur ColocPost)
        if (firstUploadedUrl != null) {
            post.setCoverUrl(firstUploadedUrl);
            postRepository.save(post);
        }
    }

    public void uploadPhoto(UUID postId, MultipartFile file, boolean isCover, UUID requesterId) {
        uploadPhotos(postId, List.of(file), requesterId);
    }

    // --- Exprimer son intérêt ---
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
            throw new DuplicateResourceException("Vous avez déjà exprimé votre intérêt");
        }

        ColocInterest interest = ColocInterest.builder()
                .postId(postId)
                .userId(userId)
                .message(message)
                .status(InterestStatus.PENDING)
                .build();

        interestRepository.save(interest);
    }

    // --- Mappers Internes avec la bonne méthode findByPostOrderByPriority ---
    private ColocPostDTO toFullDTO(ColocPost post, long total, int pending) {
        List<ColocAmenityDTO> amenities = amenityRepository.findByPost(post).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType()))
                .collect(Collectors.toList());

        // CORRECTION : Alignement parfait avec la méthode de ton repository !
        List<ColocImageDTO> images = imageRepository.findByPostOrderByPriority(post).stream()
                .map(i -> new ColocImageDTO(i.getId(), i.getUrl(), i.getSortOrder(), i.getIsCover()))
                .toList();

        String cover = post.getCoverUrl(); // Récupération directe du champ de l'entité principale

        String authorName = userRepository.findById(post.getPosterId())
                .map(u -> u.getFullName())
                .orElse("Étudiant Anonyme");

        return ColocPostDTO.builder()
                .id(post.getId())
                .posterId(post.getPosterId())
                .posterName(authorName)
                .title(post.getTitle())
                .description(post.getDescription())
                .city(post.getCity())
                .address(post.getAddress())
                .startDate(post.getStartDate())
                .spotsNeeded(post.getSpotsNeeded())
                .spotsConfirmed(post.getSpotsConfirmed())
                .housingType(post.getHousingType())
                .rentPerPerson(post.getRentPerPerson())
                .furnished(post.getFurnished())
                .status(post.getStatus())
                .coverUrl(cover)
                .totalInterests(total)
                .pendingInterests((long) pending)
                .amenities(amenities)
                .images(images)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private ColocPostDTO toSummaryDTO(ColocPost post) {
        String cover = post.getCoverUrl(); // Récupération directe

        List<ColocAmenityDTO> amenities = amenityRepository.findByPost(post).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType()))
                .collect(Collectors.toList());

        String authorName = userRepository.findById(post.getPosterId())
                .map(u -> u.getFullName())
                .orElse("Étudiant Anonyme");

        return ColocPostDTO.builder()
                .id(post.getId())
                .posterId(post.getPosterId())
                .posterName(authorName)
                .title(post.getTitle())
                .description(post.getDescription())
                .city(post.getCity())
                .address(post.getAddress())
                .startDate(post.getStartDate())
                .spotsNeeded(post.getSpotsNeeded())
                .spotsConfirmed(post.getSpotsConfirmed())
                .housingType(post.getHousingType())
                .rentPerPerson(post.getRentPerPerson())
                .furnished(post.getFurnished())
                .status(post.getStatus())
                .coverUrl(cover)
                .totalInterests(null)
                .pendingInterests(null)
                .amenities(amenities)
                .images(null)
                .createdAt(post.getCreatedAt())
                .build();
    }
}