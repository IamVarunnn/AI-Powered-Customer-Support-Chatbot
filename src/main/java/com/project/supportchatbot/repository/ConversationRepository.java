
package com.project.supportchatbot.repository;

import com.project.supportchatbot.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    List<Conversation> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Conversation> findByIdAndUserId(Integer id, Integer userId);

    Page<Conversation> findByUserId(
            Integer userId,
            Pageable pageable
    );

    void deleteByIdAndUserId(Integer id, Integer userId);
}
