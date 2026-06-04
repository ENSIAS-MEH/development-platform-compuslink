package com.compuslink.colocation.service;

import com.compuslink.colocation.client.UserClient;
import com.compuslink.colocation.dto.*;
import com.compuslink.colocation.model.*;
import com.compuslink.colocation.repository.*;
import com.compuslink.common.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class ColocPostService {

    private final ColocPostRepository postRepo;
    private final ColocImageRepository imageRepo;
    private final ColocInterestRepository interestRepo;
    private final ColocAmenityRepository amenityRepo;
    private final ColocFileStorageService fileStorage;
    private final UserClient userClient;

    public ColocPostDTO createPost(ColocPostDTO req, UUID posterId) {
        ColocPost post = postRepo.save(ColocPost.builder()
                .posterId(posterId).title(req.getTitle()).description(req.getDescription())
                .city(req.getCity()).address(req.getAddress()).startDate(req.getStartDate())
                .spotsNeeded(req.getSpotsNeeded()).housingType(req.getHousingType())
                .rentPerPerson(req.getRentPerPerson()).furnished(req.getFurnished()).build());

        List<ColocAmenityDTO> amenities = new ArrayList<>();
        if (req.getAmenities() != null) {
            req.getAmenities().forEach(a -> {
                ColocAmenity saved = amenityRepo.save(ColocAmenity.builder().post(post).amenityType(a.amenityType()).build());
                amenities.add(new ColocAmenityDTO(saved.getId(), saved.getAmenityType()));
            });
        }
        return toDTO(post, amenities, List.of(), 0L, 0L);
    }

    public Page<ColocPostDTO> browsePosts(UUID currentUserId, String city, HousingType type,
                                           Boolean furnished, Integer spots, BigDecimal rentMax, Pageable pageable) {
        return postRepo.findWithFilters(currentUserId, city, type, furnished, spots, rentMax, pageable)
                .map(this::toSummaryDTO);
    }

    public List<ColocPostDTO> getMyPosts(UUID posterId) {
        return postRepo.findByPosterId(posterId).stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    public void deletePost(UUID id, UUID userId) {
        ColocPost post = postRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        postRepo.delete(post);
    }

    public ColocPostDTO getPostDetails(UUID id, UUID currentUserId) {
        ColocPost post = postRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        long total = interestRepo.countByPostId(id);
        long pending = Objects.equals(post.getPosterId(), currentUserId)
                ? interestRepo.countByPostIdAndStatus(id, InterestStatus.PENDING) : -1;
        List<ColocAmenityDTO> amenities = amenityRepo.findByPost(post).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType())).toList();
        List<ColocImageDTO> images = imageRepo.findByPostOrderByPriority(post).stream()
                .map(i -> new ColocImageDTO(i.getId(), i.getUrl(), i.getSortOrder(), i.getIsCover())).toList();
        return toDTO(post, amenities, images, total, pending);
    }

    public void updateSpotsConfirmed(UUID postId, int count, UUID userId) {
        ColocPost post = postRepo.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        if (count < 0 || count > post.getSpotsNeeded()) throw new BusinessRuleException("Invalid spots count");
        post.setSpotsConfirmed(count);
        post.setStatus(count >= post.getSpotsNeeded() ? ColocStatus.FULL : ColocStatus.OPEN);
        postRepo.save(post);
    }

    public void handleInterestStatus(UUID interestId, InterestStatus newStatus, UUID userId) {
        ColocInterest interest = interestRepo.findById(interestId).orElseThrow(() -> new EntityNotFoundException("Interest not found"));
        ColocPost post = postRepo.findById(interest.getPostId()).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        interest.setStatus(newStatus);
        interestRepo.save(interest);
        if (newStatus == InterestStatus.ACCEPTED) {
            if (post.getSpotsConfirmed() >= post.getSpotsNeeded()) throw new BusinessRuleException("All spots filled");
            post.setSpotsConfirmed(post.getSpotsConfirmed() + 1);
            if (post.getSpotsConfirmed() >= post.getSpotsNeeded()) post.setStatus(ColocStatus.FULL);
            postRepo.save(post);
        }
    }

    public void uploadPhotos(UUID postId, List<MultipartFile> files, UUID userId) {
        ColocPost post = postRepo.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getPosterId().equals(userId)) throw new AccessDeniedException("Not authorized");
        long count = imageRepo.countByPostId(postId);
        if (count + files.size() > 10) throw new BusinessRuleException("Max 10 photos");
        for (int i = 0; i < files.size(); i++) {
            String url = fileStorage.store(files.get(i));
            boolean isCover = count == 0 && i == 0;
            imageRepo.save(ColocImage.builder().post(post).url(url).isCover(isCover).sortOrder((int)(count + i)).build());
            if (isCover) { post.setCoverUrl(url); postRepo.save(post); }
        }
    }

    public void expressInterest(UUID postId, String message, UUID userId) {
        ColocPost post = postRepo.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.getStatus() != ColocStatus.OPEN) throw new BusinessRuleException("Post not open");
        if (post.getPosterId().equals(userId)) throw new BusinessRuleException("Cannot apply to own post");
        if (interestRepo.existsByPostIdAndUserId(postId, userId)) throw new DuplicateResourceException("Already applied");
        interestRepo.save(ColocInterest.builder().postId(postId).userId(userId).message(message).build());
    }

    public boolean existsById(UUID id) { return postRepo.existsById(id); }

    private String getPosterName(UUID posterId) {
        try { return userClient.getUserSummary(posterId).getFullName(); }
        catch (Exception e) { return "Unknown"; }
    }

    private ColocPostDTO toDTO(ColocPost p, List<ColocAmenityDTO> amenities, List<ColocImageDTO> images, long total, long pending) {
        return ColocPostDTO.builder().id(p.getId()).posterId(p.getPosterId()).posterName(getPosterName(p.getPosterId()))
                .title(p.getTitle()).description(p.getDescription()).city(p.getCity()).address(p.getAddress())
                .startDate(p.getStartDate()).spotsNeeded(p.getSpotsNeeded()).spotsConfirmed(p.getSpotsConfirmed())
                .housingType(p.getHousingType()).rentPerPerson(p.getRentPerPerson()).furnished(p.getFurnished())
                .status(p.getStatus()).coverUrl(p.getCoverUrl()).totalInterests(total).pendingInterests(pending)
                .amenities(amenities).images(images).createdAt(p.getCreatedAt()).build();
    }

    private ColocPostDTO toSummaryDTO(ColocPost p) {
        List<ColocAmenityDTO> amenities = amenityRepo.findByPost(p).stream()
                .map(a -> new ColocAmenityDTO(a.getId(), a.getAmenityType())).toList();
        return ColocPostDTO.builder().id(p.getId()).posterId(p.getPosterId()).posterName(getPosterName(p.getPosterId()))
                .title(p.getTitle()).description(p.getDescription()).city(p.getCity()).address(p.getAddress())
                .startDate(p.getStartDate()).spotsNeeded(p.getSpotsNeeded()).spotsConfirmed(p.getSpotsConfirmed())
                .housingType(p.getHousingType()).rentPerPerson(p.getRentPerPerson()).furnished(p.getFurnished())
                .status(p.getStatus()).coverUrl(p.getCoverUrl()).amenities(amenities).createdAt(p.getCreatedAt()).build();
    }
}
