
package com.project.supportchatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message cannot exceed 10000 characters")
    private String content;
}
