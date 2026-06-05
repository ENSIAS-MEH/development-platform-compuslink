package com.compuslink.messaging.repository;
import com.compuslink.messaging.model.Conversation;
import com.compuslink.messaging.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
