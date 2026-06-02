package com.CompusLink.CompusLink.domain.messaging.repository;

import com.CompusLink.CompusLink.domain.messaging.model.Message;
import com.CompusLink.CompusLink.domain.messaging.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationId(@Param("conversationId") UUID conversationId);

    Message findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);
}
