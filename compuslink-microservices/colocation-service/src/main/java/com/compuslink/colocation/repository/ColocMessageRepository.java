package com.compuslink.colocation.repository;

import com.compuslink.colocation.model.ColocMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ColocMessageRepository extends JpaRepository<ColocMessage, UUID> {
    List<ColocMessage> findByInterestIdOrderByCreatedAtAsc(UUID interestId);
    void deleteByInterestId(UUID interestId);
}
