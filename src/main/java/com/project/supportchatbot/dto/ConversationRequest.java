
package com.project.supportchatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationRequest {

    @NotBlank(message = "Conversation title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
}
