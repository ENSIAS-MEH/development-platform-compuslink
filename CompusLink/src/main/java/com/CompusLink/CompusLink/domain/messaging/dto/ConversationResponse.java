package com.CompusLink.CompusLink.domain.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private UUID id;
    private UUID otherUserId;
    private String otherUserName;
    private String otherUserProfilePic;
    private String lastMessage;
    private OffsetDateTime lastMessageAt;
}
