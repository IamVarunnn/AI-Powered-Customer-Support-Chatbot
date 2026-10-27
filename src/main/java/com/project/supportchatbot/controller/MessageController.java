
package com.project.supportchatbot.controller;

import com.project.supportchatbot.dto.MessageRequest;
import com.project.supportchatbot.dto.MessageResponse;
import com.project.supportchatbot.entity.Message;
import com.project.supportchatbot.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations/{id}/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @PostMapping
    public MessageResponse sendMessage(
            @PathVariable Integer id,
            @Valid @RequestBody MessageRequest request,
            Authentication authentication) {

        Message message = messageService.sendMessage(
                id,
                authentication.getName(),
                request.getContent()
        );

        return toResponse(message);
    }


    @GetMapping
    public List<MessageResponse> getMessages(
            @PathVariable Integer id,
            Authentication authentication) {

        return messageService
                .getConversationMessages(id, authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getRole(),
                message.getCreatedAt()
        );
    }
}
