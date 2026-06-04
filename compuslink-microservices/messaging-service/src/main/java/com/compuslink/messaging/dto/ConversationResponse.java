package com.compuslink.messaging.dto;
import lombok.Builder; import lombok.Data;
import java.time.OffsetDateTime; import java.util.UUID;
@Data @Builder public class ConversationResponse { private UUID id; private UUID otherUserId; private String otherUserName; private OffsetDateTime lastMessageAt; }
