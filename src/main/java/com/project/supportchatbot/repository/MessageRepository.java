
package com.project.supportchatbot.repository;

import com.project.supportchatbot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
}
