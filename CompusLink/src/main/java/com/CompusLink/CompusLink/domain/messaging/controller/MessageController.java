package com.CompusLink.CompusLink.domain.messaging.controller;

import com.CompusLink.CompusLink.domain.messaging.dto.ConversationResponse;
import com.CompusLink.CompusLink.domain.messaging.dto.MessageRequest;
import com.CompusLink.CompusLink.domain.messaging.dto.MessageResponse;
import com.CompusLink.CompusLink.domain.messaging.service.MessageService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(@AuthenticationPrincipal UserPrincipal principal) {
        return messageService.getConversations(principal.getUser().getId());
    }

    @GetMapping("/conversations/{conversationId}")
    public List<MessageResponse> getMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return messageService.getMessages(conversationId, principal.getUser().getId());
    }

    @PostMapping("/send/{recipientId}")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID recipientId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        MessageResponse message = messageService.sendMessage(recipientId, request, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
