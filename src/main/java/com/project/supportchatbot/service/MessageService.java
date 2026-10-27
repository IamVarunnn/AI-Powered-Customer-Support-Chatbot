
package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.entity.Message;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.repository.ConversationRepository;
import com.project.supportchatbot.repository.MessageRepository;
import com.project.supportchatbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final OpenRouterService openRouterService;

    public Message sendMessage(
            Integer conversationId,
            String email,
            String content) {

        // 1. Verify conversation ownership
        Conversation conversation =
                getOwnedConversation(conversationId, email);

        // 2. Save the user's message
        Message userMessage = new Message();
        userMessage.setContent(content);
        userMessage.setRole("USER");
        userMessage.setConversation(conversation);

        messageRepository.save(userMessage);

        // 3. Generate the AI response
        String aiResponse =
                openRouterService.generateResponse(content);

        // 4. Save the assistant's response
        Message assistantMessage = new Message();
        assistantMessage.setContent(aiResponse);
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setConversation(conversation);

        return messageRepository.save(assistantMessage);
    }

    public Message saveMessage(
            Integer conversationId,
            String email,
            String content) {

        Conversation conversation =
                getOwnedConversation(conversationId, email);

        Message message = new Message();
        message.setContent(content);
        message.setRole("USER");
        message.setConversation(conversation);

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(
            Integer conversationId,
            String email) {

        getOwnedConversation(conversationId, email);

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    private Conversation getOwnedConversation(
            Integer conversationId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        return conversationRepository
                .findByIdAndUserId(conversationId, user.getId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Conversation not found"));
    }
}
