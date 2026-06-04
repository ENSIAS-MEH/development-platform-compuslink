package com.compuslink.messaging.dto;
import lombok.Builder; import lombok.Data;
import java.time.OffsetDateTime; import java.util.UUID;
@Data @Builder public class MessageResponse { private UUID id; private UUID senderId; private String content; private OffsetDateTime createdAt; private Boolean isRead; }
