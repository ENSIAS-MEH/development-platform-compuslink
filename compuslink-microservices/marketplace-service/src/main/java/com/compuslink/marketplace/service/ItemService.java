package com.compuslink.marketplace.service;

import com.compuslink.common.dto.UserSummaryDTO;
import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.marketplace.client.UserClient;
import com.compuslink.marketplace.dto.*;
import com.compuslink.marketplace.model.*;
import com.compuslink.marketplace.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepo;
    private final ItemImageRepository imageRepo;
    private final ItemInterestRepository interestRepo;
    private final FileStorageService fileStorage;
    private final UserClient userClient;

    public ItemService(ItemRepository itemRepo, ItemImageRepository imageRepo,
                       ItemInterestRepository interestRepo, FileStorageService fileStorage,
                       UserClient userClient) {
        this.itemRepo = itemRepo;
        this.imageRepo = imageRepo;
        this.interestRepo = interestRepo;
        this.fileStorage = fileStorage;
        this.userClient = userClient;
    }

    public ItemResponse createItem(ItemCreateRequest request, UUID sellerId) {
        Item item = Item.builder()
                .sellerId(sellerId).title(request.getTitle())
                .description(request.getDescription()).price(request.getPrice())
                .city(request.getCity()).condition(request.getCondition())
                .category(request.getCategory()).build();
        Item saved = itemRepo.save(item);

        if (request.getImages() != null) {
            for (int i = 0; i < request.getImages().size(); i++) {
                String url = fileStorage.store(request.getImages().get(i));
                imageRepo.save(ItemImage.builder()
                        .item(saved).url(url).sortOrder(i).isCover(i == 0).build());
            }
        }
        return toItemResponse(saved);
    }

    public List<ItemSummaryResponse> browseItems(String search, String city, String category,
                                                  ItemCondition condition, ItemStatus status) {
        Specification<Item> spec = Specification
                .where(ItemSpecification.containsSearch(search))
                .and(ItemSpecification.hasCity(city))
                .and(ItemSpecification.hasCategory(category))
                .and(ItemSpecification.hasCondition(condition))
                .and(ItemSpecification.hasStatus(status));
        return itemRepo.findAll(spec).stream().map(this::toSummary).toList();
    }

    public ItemResponse getItem(UUID id) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        return toItemResponse(item);
    }

    public ItemResponse updateItem(UUID id, ItemUpdateRequest request, UUID requesterId) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getSellerId().equals(requesterId))
            throw new IllegalArgumentException("Only the seller can update this item");

        if (request.getTitle() != null) item.setTitle(request.getTitle());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getCity() != null) item.setCity(request.getCity());
        if (request.getCondition() != null) item.setCondition(request.getCondition());
        if (request.getCategory() != null) item.setCategory(request.getCategory());

        if (request.getRemoveImageIds() != null) {
            for (UUID imgId : request.getRemoveImageIds()) {
                imageRepo.findById(imgId).ifPresent(img -> {
                    fileStorage.delete(img.getUrl());
                    imageRepo.delete(img);
                });
            }
        }
        if (request.getNewImages() != null) {
            int nextOrder = imageRepo.findByItemOrderBySortOrderAsc(item).stream()
                    .mapToInt(ItemImage::getSortOrder).max().orElse(-1) + 1;
            for (int i = 0; i < request.getNewImages().size(); i++) {
                String url = fileStorage.store(request.getNewImages().get(i));
                imageRepo.save(ItemImage.builder()
                        .item(item).url(url).sortOrder(nextOrder + i).isCover(false).build());
            }
        }
        return toItemResponse(itemRepo.save(item));
    }

    public void closeItem(UUID id, UUID requesterId) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getSellerId().equals(requesterId))
            throw new IllegalArgumentException("Only the seller can close this item");
        item.setStatus(ItemStatus.CLOSED);
        itemRepo.save(item);
    }

    public ItemResponse markAsSold(UUID id, UUID requesterId) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getSellerId().equals(requesterId))
            throw new IllegalArgumentException("Only the seller can mark as sold");
        item.setStatus(ItemStatus.SOLD);
        return toItemResponse(itemRepo.save(item));
    }

    public ItemInterestResponse expressInterest(UUID itemId, ItemInterestRequest request, UUID buyerId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (item.getSellerId().equals(buyerId))
            throw new IllegalArgumentException("Cannot express interest in own item");
        if (item.getStatus() != ItemStatus.OPEN)
            throw new IllegalArgumentException("Item is not available");
        if (interestRepo.existsByItemIdAndUserId(itemId, buyerId))
            throw new IllegalArgumentException("Already expressed interest");

        ItemInterest saved = interestRepo.save(ItemInterest.builder()
                .itemId(itemId).userId(buyerId).message(request.getMessage()).build());
        return toInterestResponse(saved);
    }

    public List<ItemInterestResponse> getItemInterests(UUID itemId, UUID requesterId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getSellerId().equals(requesterId))
            throw new IllegalArgumentException("Only seller can view interests");
        return interestRepo.findByItemId(itemId).stream().map(this::toInterestResponse).toList();
    }

    public List<ItemSummaryResponse> getMyItems(UUID sellerId) {
        return itemRepo.findBySellerId(sellerId).stream().map(this::toSummary).toList();
    }

    // Internal endpoint for common-service to check existence
    public boolean existsById(UUID id) {
        return itemRepo.existsById(id);
    }

    private ItemResponse toItemResponse(Item item) {
        List<ItemImageResponse> images = imageRepo.findByItemOrderBySortOrderAsc(item).stream()
                .map(img -> ItemImageResponse.builder()
                        .id(img.getId()).url(img.getUrl())
                        .sortOrder(img.getSortOrder()).isCover(img.getIsCover()).build())
                .toList();

        String sellerName = getUsername(item.getSellerId());
        return ItemResponse.builder()
                .id(item.getId()).sellerId(item.getSellerId()).sellerName(sellerName)
                .title(item.getTitle()).description(item.getDescription())
                .price(item.getPrice()).city(item.getCity())
                .condition(item.getCondition()).category(item.getCategory())
                .status(item.getStatus()).images(images)
                .createdAt(item.getCreatedAt()).updatedAt(item.getUpdatedAt()).build();
    }

    private ItemSummaryResponse toSummary(Item item) {
        String coverUrl = imageRepo.findFirstByItemAndIsCoverTrue(item)
                .map(ItemImage::getUrl).orElse(null);
        return ItemSummaryResponse.builder()
                .id(item.getId()).title(item.getTitle()).price(item.getPrice())
                .city(item.getCity()).condition(item.getCondition())
                .category(item.getCategory()).status(item.getStatus())
                .coverImageUrl(coverUrl).createdAt(item.getCreatedAt()).build();
    }

    private ItemInterestResponse toInterestResponse(ItemInterest interest) {
        String buyerName = getUsername(interest.getUserId());
        return ItemInterestResponse.builder()
                .id(interest.getId()).itemId(interest.getItemId())
                .buyerId(interest.getUserId()).buyerName(buyerName)
                .message(interest.getMessage()).createdAt(interest.getCreatedAt()).build();
    }

    private String getUsername(UUID userId) {
        try {
            UserSummaryDTO user = userClient.getUserSummary(userId);
            return user.getFullName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
