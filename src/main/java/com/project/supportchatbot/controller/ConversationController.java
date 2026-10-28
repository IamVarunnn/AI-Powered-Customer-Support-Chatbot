
package com.project.supportchatbot.controller;

import com.project.supportchatbot.dto.ConversationRequest;
import com.project.supportchatbot.dto.ConversationResponse;
import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.service.ConversationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

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
    public Page<ConversationResponse> getUserConversations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 50");
        }

        return conversationService
                .getUserConversations(
                        authentication.getName(),
                        page,
                        size
                )
                .map(conversation -> new ConversationResponse(
                        conversation.getId(),
                        conversation.getTitle(),
                        conversation.getCreatedAt()
                ));
    }

    private ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt()
        );
    }


    @GetMapping("/{id}")
    public ConversationResponse getConversation(
            @PathVariable Integer id,
            Authentication authentication) {

        Conversation conversation = conversationService.getConversation(
                id,
                authentication.getName()
        );

        return toResponse(conversation);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Integer id,
            Authentication authentication) {

        conversationService.deleteConversation(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

}
