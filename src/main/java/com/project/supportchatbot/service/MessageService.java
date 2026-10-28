
package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.Conversation;
import com.project.supportchatbot.entity.Message;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.repository.ConversationRepository;
import com.project.supportchatbot.repository.MessageRepository;
import com.project.supportchatbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        // Generate a title from the first message
        if ("New Conversation".equals(conversation.getTitle())) {

            String title = content.length() > 40
                    ? content.substring(0, 40) + "..."
                    : content;

            conversation.setTitle(title);
            conversationRepository.save(conversation);
        }

        // 2. Load previous messages before saving the new one
        List<Message> allPreviousMessages =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );

        int historyLimit = 10;

        int startIndex = Math.max(
                0,
                allPreviousMessages.size() - historyLimit
        );

        List<Message> previousMessages =
                allPreviousMessages.subList(
                        startIndex,
                        allPreviousMessages.size()
                );

        List<Map<String, String>> conversationHistory =
                new ArrayList<>();

        for (Message message : previousMessages) {
            conversationHistory.add(Map.of(
                    "role", message.getRole().equals("USER")
                            ? "user" : "assistant",
                    "content", message.getContent()
            ));
        }

        // 3. Save the new user message
        Message userMessage = new Message();
        userMessage.setContent(content);
        userMessage.setRole("USER");
        userMessage.setConversation(conversation);

        messageRepository.save(userMessage);

        // 4. Add the current message to the history sent to the AI
        conversationHistory.add(Map.of(
                "role", "user",
                "content", content
        ));

        // 5. Generate the AI response using the full history
        String aiResponse =
                openRouterService.generateResponse(conversationHistory);

        // 6. Save the assistant's response
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
