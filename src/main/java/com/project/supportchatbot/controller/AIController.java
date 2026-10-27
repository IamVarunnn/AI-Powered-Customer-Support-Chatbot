
package com.project.supportchatbot.controller;

import com.project.supportchatbot.service.OpenRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final OpenRouterService openRouterService;

    @PostMapping("/test")
    public Map<String, String> testAI(
            @RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt must not be empty"
            );
        }

        return Map.of(
                "response",
                openRouterService.generateResponse(prompt)
        );
    }
}
