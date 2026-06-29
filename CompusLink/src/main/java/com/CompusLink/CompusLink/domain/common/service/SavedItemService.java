package com.CompusLink.CompusLink.domain.common.service;

import com.CompusLink.CompusLink.domain.common.dto.SaveRequest;
import com.CompusLink.CompusLink.domain.common.dto.SaveResponse;
import com.CompusLink.CompusLink.domain.common.model.SavedItem;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import com.CompusLink.CompusLink.domain.common.repository.SavedItemRepository;
import com.CompusLink.CompusLink.domain.colocation.repository.ColocPostRepository;
import com.CompusLink.CompusLink.domain.event.repository.EventRepository;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemImage;
import com.CompusLink.CompusLink.domain.marketplace.repository.ItemRepository;
import com.CompusLink.CompusLink.domain.marketplace.repository.ItemImageRepository;
import com.CompusLink.CompusLink.domain.offer.repository.OfferRepository;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SavedItemService {

    private final SavedItemRepository savedItemRepo;
    private final TargetValidationService targetValidation;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ColocPostRepository colocPostRepository;
    private final OfferRepository offerRepository;
    private final EventRepository eventRepository;

    public SavedItemService(SavedItemRepository savedItemRepo, TargetValidationService targetValidation,
                          ItemRepository itemRepository, ItemImageRepository itemImageRepository,
                          ColocPostRepository colocPostRepository, OfferRepository offerRepository,
                          EventRepository eventRepository) {
        this.savedItemRepo = savedItemRepo;
        this.targetValidation = targetValidation;
        this.itemRepository = itemRepository;
        this.itemImageRepository = itemImageRepository;
        this.colocPostRepository = colocPostRepository;
        this.offerRepository = offerRepository;
        this.eventRepository = eventRepository;
    }

    public SaveResponse save(SaveRequest request, UUID userId) {
        targetValidation.validate(request.getTargetType(), request.getTargetId());

        if (savedItemRepo.existsByUserIdAndTargetTypeAndTargetId(userId, request.getTargetType(), request.getTargetId())) {
            throw new DuplicateResourceException("Already saved");
        }

        SavedItem item = SavedItem.builder()
                .userId(userId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .build();

        return toResponse(savedItemRepo.save(item));
    }

    public void unsave(UUID userId, TargetType targetType, UUID targetId) {
        SavedItem item = savedItemRepo.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .orElseThrow(() -> new EntityNotFoundException("Saved item not found"));
        savedItemRepo.delete(item);
    }

    public List<SaveResponse> getMySaved(UUID userId, TargetType targetType) {
        List<SavedItem> items = targetType == null
                ? savedItemRepo.findByUserId(userId)
                : savedItemRepo.findByUserIdAndTargetType(userId, targetType);
        return items.stream().map(this::toResponse).toList();
    }

    private SaveResponse toResponse(SavedItem s) {
        SaveResponse.SaveResponseBuilder builder = SaveResponse.builder()
                .id(s.getId())
                .targetType(s.getTargetType())
                .targetId(s.getTargetId())
                .createdAt(s.getCreatedAt());

        switch (s.getTargetType()) {
            case ITEM -> itemRepository.findById(s.getTargetId()).ifPresent(item -> {
                builder.title(item.getTitle())
                        .price(item.getPrice())
                        .city(item.getCity())
                        .category(item.getCategory())
                        .condition(item.getCondition().toString())
                        .status(item.getStatus().toString());
                Optional<ItemImage> coverImage = itemImageRepository.findFirstByItemAndIsCoverTrue(item);
                coverImage.ifPresent(img -> builder.coverImageUrl(img.getUrl()));
            });
            case COLOC -> colocPostRepository.findById(s.getTargetId()).ifPresent(post -> {
                builder.title(post.getTitle())
                        .price(post.getRentPerPerson())
                        .city(post.getCity())
                        .coverImageUrl(post.getCoverUrl())
                        .status(post.getStatus().toString());
            });
            case OFFER -> offerRepository.findById(s.getTargetId()).ifPresent(offer -> {
                builder.title(offer.getTitle())
                        .city(offer.getCity())
                        .category(offer.getType().toString())
                        .status(offer.getStatus().toString());
            });
            case EVENT -> eventRepository.findById(s.getTargetId()).ifPresent(event -> {
                builder.title(event.getTitle())
                        .city(event.getCity())
                        .category(event.getCategory().toString())
                        .coverImageUrl(event.getCoverUrl());
            });
            case USER -> {}
        }

        return builder.build();
    }
}
