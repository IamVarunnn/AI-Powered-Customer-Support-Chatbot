
package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.repository.ConversationRepository;
import com.project.supportchatbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.project.supportchatbot.repository.MessageRepository;
import java.util.List;

@Service
@AllArgsConstructor
public class ConversationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public Conversation createConversation(String email, String title) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

        Conversation conversation = new Conversation();
        conversation.setTitle(
                title == null || title.isBlank()
                        ? "New Conversation"
                        : title
        );
        conversation.setUser(user);

        return conversationRepository.save(conversation);
    }

    public Page<Conversation> getUserConversations(
            String email,
            int page,
            int size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"));

        Pageable pageable = PageRequest.of(page, size);

        return conversationRepository.findByUserId(
                user.getId(),
                pageable
        );
    }


    public Conversation getConversation(Integer conversationId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

        return conversationRepository
                .findByIdAndUserId(conversationId, user.getId())
                .orElseThrow(() ->
                        new java.util.NoSuchElementException("Conversation not found"));
    }

    public void deleteConversation(Integer conversationId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"));

        Conversation conversation = conversationRepository
                .findByIdAndUserId(conversationId, user.getId())
                .orElseThrow(() ->
                        new java.util.NoSuchElementException(
                                "Conversation not found"));

        messageRepository.deleteByConversationId(conversation.getId());

        conversationRepository.delete(conversation);
    }

    public Conversation renameConversation(
            Integer conversationId,
            String email,
            String newTitle) {

        Conversation conversation =
                getConversation(conversationId, email);

        conversation.setTitle(newTitle.trim());

        return conversationRepository.save(conversation);
    }

}
