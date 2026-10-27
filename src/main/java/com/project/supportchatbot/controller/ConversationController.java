
package com.project.supportchatbot.controller;

import com.project.supportchatbot.dto.ConversationRequest;
import com.project.supportchatbot.dto.ConversationResponse;
import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ConversationResponse createConversation(
            @Valid @RequestBody ConversationRequest request,
            Authentication authentication) {

        Conversation conversation = conversationService.createConversation(
                authentication.getName(),
                request.getTitle()
        );

        return toResponse(conversation);
    }

    @GetMapping
    public List<ConversationResponse> getUserConversations(
            Authentication authentication) {

        return conversationService
                .getUserConversations(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt()
        );
    }
}
