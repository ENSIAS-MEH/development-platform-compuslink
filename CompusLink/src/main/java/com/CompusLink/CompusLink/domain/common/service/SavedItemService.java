package com.CompusLink.CompusLink.domain.common.service;

import com.CompusLink.CompusLink.domain.common.dto.SaveRequest;
import com.CompusLink.CompusLink.domain.common.dto.SaveResponse;
import com.CompusLink.CompusLink.domain.common.model.SavedItem;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import com.CompusLink.CompusLink.domain.common.repository.SavedItemRepository;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SavedItemService {

    private final SavedItemRepository savedItemRepo;
    private final TargetValidationService targetValidation;

    public SavedItemService(SavedItemRepository savedItemRepo, TargetValidationService targetValidation) {
        this.savedItemRepo = savedItemRepo;
        this.targetValidation = targetValidation;
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
        return SaveResponse.builder()
                .id(s.getId())
                .targetType(s.getTargetType())
                .targetId(s.getTargetId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
