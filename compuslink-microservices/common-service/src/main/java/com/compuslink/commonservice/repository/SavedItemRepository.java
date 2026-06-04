package com.compuslink.commonservice.repository;
import com.compuslink.commonservice.model.SavedItem;
import com.compuslink.commonservice.model.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SavedItemRepository extends JpaRepository<SavedItem, UUID> {
    boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType type, UUID targetId);
    Optional<SavedItem> findByUserIdAndTargetTypeAndTargetId(UUID userId, TargetType type, UUID targetId);
    List<SavedItem> findByUserId(UUID userId);
    List<SavedItem> findByUserIdAndTargetType(UUID userId, TargetType type);
}
