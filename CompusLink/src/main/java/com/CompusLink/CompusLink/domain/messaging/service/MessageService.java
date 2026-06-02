package com.CompusLink.CompusLink.domain.messaging.service;

import com.CompusLink.CompusLink.domain.messaging.dto.ConversationResponse;
import com.CompusLink.CompusLink.domain.messaging.dto.MessageRequest;
import com.CompusLink.CompusLink.domain.messaging.dto.MessageResponse;
import com.CompusLink.CompusLink.domain.messaging.model.Conversation;
import com.CompusLink.CompusLink.domain.messaging.model.Message;
import com.CompusLink.CompusLink.domain.messaging.repository.ConversationRepository;
import com.CompusLink.CompusLink.domain.messaging.repository.MessageRepository;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public List<ConversationResponse> getConversations(UUID userId) {
        List<Conversation> conversations = conversationRepository.findAllByUserId(userId);
        return conversations.stream().map(conv -> toConversationResponse(conv, userId)).toList();
    }

    public List<MessageResponse> getMessages(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            throw new EntityNotFoundException("Unauthorized");
        }

        List<Message> messages = messageRepository.findByConversationId(conversationId);
        return messages.stream().map(this::toMessageResponse).toList();
    }

    public MessageResponse sendMessage(UUID otherUserId, MessageRequest request, UUID senderId) {
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Users receiver = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

        Conversation conversation = conversationRepository.findConversation(senderId, otherUserId)
                .orElseGet(() -> {
                    Conversation newConv = Conversation.builder()
                            .user1(sender)
                            .user2(receiver)
                            .createdAt(OffsetDateTime.now())
                            .build();
                    return conversationRepository.save(newConv);
                });

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);
        conversation.setLastMessageAt(OffsetDateTime.now());
        conversationRepository.save(conversation);

        return toMessageResponse(saved);
    }

    private ConversationResponse toConversationResponse(Conversation conversation, UUID currentUserId) {
        Users otherUser = conversation.getUser1().getId().equals(currentUserId) ? conversation.getUser2() : conversation.getUser1();
        Message lastMessage = messageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .otherUserId(otherUser.getId())
                .otherUserName(otherUser.getFullName())
                .otherUserProfilePic(otherUser.getProfilePicUrl())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .lastMessageAt(conversation.getLastMessageAt())
                .build();
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.getIsRead())
                .build();
    }
}
