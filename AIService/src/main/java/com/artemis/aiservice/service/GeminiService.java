package com.artemis.aiservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.artemis.aiservice.dto.TaskSuggestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService
{
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.url}")
    private String groqUrl;

    @Value("${groq.model}")
    private String model;

    public List<TaskSuggestionDto> decompose(String goal)
    {
        String prompt = """
                Ты помощник менеджера задач. Пользователь хочет достичь цели: "%s".
                Разбей эту цель на конкретные подзадачи для выполнения в рамках проекта.
                Верни ТОЛЬКО JSON массив из 3 до 6 задач без каких-либо пояснений. Каждая задача содержит поля:
                - name: название задачи (до 50 символов)
                - description: описание (1-2 предложения)
                - importance: одно из LOW, INTERMEDIATE, HIGH
                - taskStatus: всегда "PLANING"
                """.formatted(goal);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                )),
                "temperature", 0.7,
                "max_tokens", 1024
        );

        String responseBody = restClient.post()
                .uri(groqUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content").asText();

            String json = text.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").trim();
            }

            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response: " + e.getMessage(), e);
        }
    }
}
