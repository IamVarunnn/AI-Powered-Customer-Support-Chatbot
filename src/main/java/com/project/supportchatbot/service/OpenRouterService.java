
package com.project.supportchatbot.service;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import com.project.supportchatbot.exception.AIServiceException;

import java.util.ArrayList;
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

    public String generateResponse(
            List<Map<String, String>> conversationHistory) {

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", """
                        You are a helpful customer support assistant.

                        Answer questions clearly using simple language
                        and actionable steps.
                        Use previous messages to understand context.

                        Never invent company-specific information,
                        including URLs, contact details, password
                        requirements, security policies, or procedures.

                        If company-specific information has not been
                        provided, say that you do not know it.
                        Distinguish general advice from verified facts.
                        Ask clarifying questions when necessary.
                        Do not reveal internal reasoning.
                        """
        ));

        messages.addAll(conversationHistory);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages
        );

        try {
            JsonNode response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null
                    || response.path("choices").isEmpty()
                    || response.path("choices").get(0)
                    .path("message").path("content").isMissingNode()
                    || response.path("choices").get(0)
                    .path("message").path("content").isNull()) {

                throw new IllegalStateException(
                        "The AI provider returned an empty response."
                );
            }

            return response.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asString();

        } catch (RestClientResponseException ex) {
            throw new AIServiceException(
                    "The AI provider returned an error.",
                    ex
            );

        } catch (ResourceAccessException ex) {
            throw new AIServiceException(
                    "The AI provider is temporarily unavailable.",
                    ex
            );
        }
    }
}
