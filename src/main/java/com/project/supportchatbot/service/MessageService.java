
package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.entity.Message;
import com.project.supportchatbot.repository.ConversationRepository;
import com.project.supportchatbot.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public Message saveMessage(
            Integer conversationId,
            String content,
            String role) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException("Conversation not found"));

        Message message = new Message();
        message.setContent(content);
        message.setRole(role);
        message.setConversation(conversation);

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(Integer conversationId) {
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}
