package com.compuslink.commonservice.service;

import com.compuslink.common.exception.*;
import com.compuslink.commonservice.dto.*;
import com.compuslink.commonservice.model.*;
import com.compuslink.commonservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List; import java.util.UUID;

@Service @RequiredArgsConstructor
public class SavedItemService {
    private final SavedItemRepository repo;
    private final TargetValidationService validation;

    public SavedItemResponse save(SaveRequest req, UUID userId) {
        validation.validate(req.getTargetType(), req.getTargetId());
        if (repo.existsByUserIdAndTargetTypeAndTargetId(userId, req.getTargetType(), req.getTargetId()))
            throw new DuplicateResourceException("Already saved");
        SavedItem s = repo.save(SavedItem.builder().userId(userId).targetType(req.getTargetType()).targetId(req.getTargetId()).build());
        return new SavedItemResponse(s.getId(), s.getTargetType(), s.getTargetId(), s.getCreatedAt());
    }

    public void unsave(UUID userId, TargetType type, UUID targetId) {
        SavedItem item = repo.findByUserIdAndTargetTypeAndTargetId(userId, type, targetId)
                .orElseThrow(() -> new EntityNotFoundException("Saved item not found"));
        repo.delete(item);
    }

    public List<SavedItemResponse> getMySaved(UUID userId, TargetType type) {
        List<SavedItem> items = type == null ? repo.findByUserId(userId) : repo.findByUserIdAndTargetType(userId, type);
        return items.stream().map(s -> new SavedItemResponse(s.getId(), s.getTargetType(), s.getTargetId(), s.getCreatedAt())).toList();
    }
}
