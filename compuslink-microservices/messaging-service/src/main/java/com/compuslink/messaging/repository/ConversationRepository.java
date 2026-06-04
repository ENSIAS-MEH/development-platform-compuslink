package com.compuslink.messaging.repository;
import com.compuslink.messaging.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c WHERE c.user1Id = :uid OR c.user2Id = :uid ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findByUserId(UUID uid);
    @Query("SELECT c FROM Conversation c WHERE (c.user1Id = :a AND c.user2Id = :b) OR (c.user1Id = :b AND c.user2Id = :a)")
    Optional<Conversation> findByUsers(UUID a, UUID b);
}
