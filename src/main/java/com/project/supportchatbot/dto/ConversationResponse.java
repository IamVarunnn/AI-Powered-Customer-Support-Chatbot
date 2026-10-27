
package com.project.supportchatbot.dto;

import java.time.LocalDateTime;

public record ConversationResponse(
        Integer id,
        String title,
        LocalDateTime createdAt
) {}
