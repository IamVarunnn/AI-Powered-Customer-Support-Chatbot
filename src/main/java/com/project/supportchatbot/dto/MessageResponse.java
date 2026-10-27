
package com.project.supportchatbot.dto;

import java.time.LocalDateTime;

public record MessageResponse(
        Integer id,
        String content,
        String role,
        LocalDateTime createdAt
) {}
