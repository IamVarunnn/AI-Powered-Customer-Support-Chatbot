
package com.project.supportchatbot.service;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenRouterService {

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    private final RestClient restClient;

    public String generateResponse(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                You are a helpful customer support assistant.
                                Answer the user's question clearly and accurately.
                                Use simple language and actionable steps.
                                If the question is ambiguous, ask for clarification.
                                Never invent company-specific policies or details.
                                Do not reveal internal reasoning.
                                """
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        JsonNode response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);
        System.out.println("OpenRouter response: " + response);

        if (response == null
                || response.path("choices").isEmpty()
                || response.path("choices").get(0)
                .path("message").path("content").isMissingNode()
                || response.path("choices").get(0)
                .path("message").path("content").isNull()) {

            throw new IllegalStateException(
                    "OpenRouter returned an empty response"
            );
        }

        return response.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asString();
    }
}
