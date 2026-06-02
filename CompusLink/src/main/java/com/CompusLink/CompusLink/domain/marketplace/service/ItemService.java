package com.CompusLink.CompusLink.domain.marketplace.service;

import com.CompusLink.CompusLink.domain.marketplace.dto.*;
import com.CompusLink.CompusLink.domain.marketplace.model.*;
import com.CompusLink.CompusLink.domain.marketplace.repository.*;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ItemService {

    @Autowired private ItemRepository itemRepository;
    @Autowired private ItemImageRepository itemImageRepository;
    @Autowired private ItemInterestRepository itemInterestRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FileStorageService fileStorageService;

    public ItemResponse createItem(ItemCreateRequest request, UUID sellerId) {
        Item item = Item.builder()
                .sellerId(sellerId)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .city(request.getCity())
                .condition(request.getCondition())
                .category(request.getCategory())
                .build();

        Item saved = itemRepository.save(item);

        List<org.springframework.web.multipart.MultipartFile> files = request.getImages();
        for (int i = 0; i < files.size(); i++) {
            String url = fileStorageService.store(files.get(i));
            itemImageRepository.save(ItemImage.builder()
                    .item(saved)
                    .url(url)
                    .sortOrder(i)
                    .isCover(i == 0)
                    .build());
        }

        return toItemResponse(saved);
    }

    public List<ItemSummaryResponse> browseItems(String search, String city, String category, ItemCondition condition, ItemStatus status) {
        Specification<Item> spec = Specification
                .where(ItemSpecification.containsSearch(search))
                .and(ItemSpecification.hasCity(city))
                .and(ItemSpecification.hasCategory(category))
                .and(ItemSpecification.hasCondition(condition))
                .and(ItemSpecification.hasStatus(status));

        return itemRepository.findAll(spec).stream()
                .map(this::toItemSummaryResponse)
                .toList();
    }

    public ItemResponse getItem(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return toItemResponse(item);
    }

    public ItemResponse updateItem(UUID id, ItemUpdateRequest request, UUID requesterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getSellerId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the seller can update this item");
        }
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new IllegalArgumentException("Only open items can be updated");
        }

        if (request.getTitle() != null) item.setTitle(request.getTitle());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getCity() != null) item.setCity(request.getCity());
        if (request.getCondition() != null) item.setCondition(request.getCondition());
        if (request.getCategory() != null) item.setCategory(request.getCategory());

        if (request.getRemoveImageIds() != null) {
            for (UUID imageId : request.getRemoveImageIds()) {
                itemImageRepository.findById(imageId).ifPresent(img -> {
                    fileStorageService.delete(img.getUrl());
                    itemImageRepository.delete(img);
                });
            }
        }

        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {
            int nextOrder = itemImageRepository.findByItemOrderBySortOrderAsc(item).stream()
                    .mapToInt(ItemImage::getSortOrder)
                    .max()
                    .orElse(-1) + 1;

            for (int i = 0; i < request.getNewImages().size(); i++) {
                String url = fileStorageService.store(request.getNewImages().get(i));
                itemImageRepository.save(ItemImage.builder()
                        .item(item)
                        .url(url)
                        .sortOrder(nextOrder + i)
                        .isCover(false)
                        .build());
            }
        }

        return toItemResponse(itemRepository.save(item));
    }

    public void closeItem(UUID id, UUID requesterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getSellerId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the seller can close this item");
        }

        item.setStatus(ItemStatus.CLOSED);
        itemRepository.save(item);
    }

    public ItemResponse markAsSold(UUID id, UUID requesterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getSellerId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the seller can mark this item as sold");
        }
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new IllegalArgumentException("Item is not currently open");
        }

        item.setStatus(ItemStatus.SOLD);
        return toItemResponse(itemRepository.save(item));
    }

    public ItemInterestResponse expressInterest(UUID itemId, ItemInterestRequest request, UUID buyerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (item.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("Sellers cannot express interest in their own items");
        }
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new IllegalArgumentException("This item is no longer available");
        }
        if (itemInterestRepository.existsByItemIdAndUserId(itemId, buyerId)) {
            throw new IllegalArgumentException("You have already expressed interest in this item");
        }

        ItemInterest saved = itemInterestRepository.save(ItemInterest.builder()
                .itemId(itemId)
                .userId(buyerId)
                .message(request.getMessage())
                .build());

        return toInterestResponse(saved);
    }

    public List<ItemInterestResponse> getItemInterests(UUID itemId, UUID requesterId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getSellerId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the seller can view interests on this item");
        }

        return itemInterestRepository.findByItemId(itemId).stream()
                .map(this::toInterestResponse)
                .toList();
    }

    public List<ItemSummaryResponse> getMyItems(UUID sellerId) {
        return itemRepository.findBySellerId(sellerId).stream()
                .map(this::toItemSummaryResponse)
                .toList();
    }

    public List<ItemSummaryResponse> getMyInterests(UUID userId) {
        return itemInterestRepository.findByUserId(userId).stream()
                .map(interest -> itemRepository.findById(interest.getItemId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toItemSummaryResponse)
                .toList();
    }

    private ItemResponse toItemResponse(Item item) {
        List<ItemImageResponse> images = itemImageRepository.findByItemOrderBySortOrderAsc(item).stream()
                .map(img -> ItemImageResponse.builder()
                        .id(img.getId())
                        .url(img.getUrl())
                        .sortOrder(img.getSortOrder())
                        .isCover(img.getIsCover())
                        .build())
                .toList();

        String sellerName = userRepository.findById(item.getSellerId())
                .map(Users::getFullName)
                .orElse("Unknown");

        return ItemResponse.builder()
                .id(item.getId())
                .sellerId(item.getSellerId())
                .sellerName(sellerName)
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .city(item.getCity())
                .condition(item.getCondition())
                .category(item.getCategory())
                .status(item.getStatus())
                .images(images)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private ItemSummaryResponse toItemSummaryResponse(Item item) {
        String coverUrl = itemImageRepository.findFirstByItemAndIsCoverTrue(item)
                .map(ItemImage::getUrl)
                .orElse(null);

        return ItemSummaryResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .price(item.getPrice())
                .city(item.getCity())
                .condition(item.getCondition())
                .category(item.getCategory())
                .status(item.getStatus())
                .coverImageUrl(coverUrl)
                .createdAt(item.getCreatedAt())
                .build();
    }

    private ItemInterestResponse toInterestResponse(ItemInterest interest) {
        String buyerName = userRepository.findById(interest.getUserId())
                .map(Users::getFullName)
                .orElse("Unknown");

        return ItemInterestResponse.builder()
                .id(interest.getId())
                .itemId(interest.getItemId())
                .buyerId(interest.getUserId())
                .buyerName(buyerName)
                .message(interest.getMessage())
                .createdAt(interest.getCreatedAt())
                .build();
    }
}
