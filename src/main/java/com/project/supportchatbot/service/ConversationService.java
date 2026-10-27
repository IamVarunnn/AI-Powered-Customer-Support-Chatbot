
package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.repository.ConversationRepository;
import com.project.supportchatbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public Conversation createConversation(String email, String title) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

        Conversation conversation = new Conversation();
        conversation.setTitle(title);
        conversation.setUser(user);

        return conversationRepository.save(conversation);
    }

    public List<Conversation> getUserConversations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

        return conversationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
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

}
