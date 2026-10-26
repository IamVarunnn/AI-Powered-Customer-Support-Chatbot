package com.project.supportchatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
