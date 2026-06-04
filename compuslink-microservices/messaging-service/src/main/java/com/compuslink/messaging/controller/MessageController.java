package com.compuslink.messaging.controller;

import com.compuslink.messaging.dto.*;
import com.compuslink.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController @RequiredArgsConstructor
public class MessageController {
    private final MessageService service;

    @GetMapping("/api/conversations")
    public List<ConversationResponse> conversations(@RequestHeader("X-User-Id") UUID userId) { return service.getConversations(userId); }

    @GetMapping("/api/conversations/{id}/messages")
    public List<MessageResponse> messages(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) { return service.getMessages(id, userId); }

    @PostMapping("/api/messages/{recipientId}")
    public MessageResponse send(@PathVariable UUID recipientId, @RequestBody MessageRequest req, @RequestHeader("X-User-Id") UUID userId) { return service.sendMessage(recipientId, req, userId); }
}
