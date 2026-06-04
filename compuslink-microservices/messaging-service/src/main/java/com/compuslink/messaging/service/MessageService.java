package com.compuslink.messaging.service;

import com.compuslink.common.exception.*;
import com.compuslink.messaging.client.UserClient;
import com.compuslink.messaging.dto.*;
import com.compuslink.messaging.model.*;
import com.compuslink.messaging.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class MessageService {
    private final ConversationRepository convoRepo;
    private final MessageRepository msgRepo;
    private final UserClient userClient;

    public List<ConversationResponse> getConversations(UUID userId) {
        return convoRepo.findByUserId(userId).stream().map(c -> {
            UUID otherId = c.getUser1Id().equals(userId) ? c.getUser2Id() : c.getUser1Id();
            String name; try { name = userClient.getUserSummary(otherId).getFullName(); } catch (Exception e) { name = "Unknown"; }
            return ConversationResponse.builder().id(c.getId()).otherUserId(otherId)
                    .otherUserName(name).lastMessageAt(c.getLastMessageAt()).build();
        }).toList();
    }

    public List<MessageResponse> getMessages(UUID conversationId, UUID userId) {
        Conversation c = convoRepo.findById(conversationId).orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
        if (!c.getUser1Id().equals(userId) && !c.getUser2Id().equals(userId)) throw new AccessDeniedException("Not authorized");
        return msgRepo.findByConversationOrderByCreatedAtAsc(c).stream()
                .map(m -> MessageResponse.builder().id(m.getId()).senderId(m.getSenderId())
                        .content(m.getContent()).createdAt(m.getCreatedAt()).isRead(m.getIsRead()).build())
                .toList();
    }

    public MessageResponse sendMessage(UUID recipientId, MessageRequest req, UUID senderId) {
        Conversation convo = convoRepo.findByUsers(senderId, recipientId)
                .orElseGet(() -> convoRepo.save(Conversation.builder().user1Id(senderId).user2Id(recipientId).build()));
        Message msg = msgRepo.save(Message.builder().conversation(convo).senderId(senderId).content(req.getContent()).build());
        convo.setLastMessageAt(OffsetDateTime.now());
        convoRepo.save(convo);
        return MessageResponse.builder().id(msg.getId()).senderId(senderId).content(msg.getContent()).createdAt(msg.getCreatedAt()).isRead(false).build();
    }
}
