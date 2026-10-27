
package com.project.supportchatbot.repository;

import com.project.supportchatbot.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    List<Conversation> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Conversation> findByIdAndUserId(Integer id, Integer userId);
}
