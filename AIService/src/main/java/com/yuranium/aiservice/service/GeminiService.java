package com.yuranium.aiservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuranium.aiservice.dto.TaskSuggestionDto;
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

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String geminiUrl;

    public List<TaskSuggestionDto> decompose(String goal)
    {
        String prompt = """
                Ты помощник менеджера задач. Пользователь хочет достичь цели: "%s".
                Разбей эту цель на конкретные подзадачи для выполнения в рамках проекта.
                Верни JSON массив из 3 до 6 задач. Каждая задача должна содержать поля:
                - name: название задачи (до 50 символов)
                - description: описание (1-2 предложения)
                - importance: важность — одно из: LOW, INTERMEDIATE, HIGH
                - taskStatus: всегда "PLANING"
                Отвечай только JSON массивом без пояснений.
                """.formatted(goal);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String url = geminiUrl + "?key=" + apiKey;

        String responseBody = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();
            return objectMapper.readValue(text, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }
}
