package com.artemis.aiservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.artemis.aiservice.controller.AiController;
import com.artemis.aiservice.dto.DecomposeRequest;
import com.artemis.aiservice.dto.TaskSuggestionDto;
import com.artemis.aiservice.service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AiControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private AiController aiController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void decompose_ReturnsSuggestions() throws Exception
    {
        String goal = "Создать REST API для задач";
        DecomposeRequest request = new DecomposeRequest(goal, UUID.randomUUID());
        List<TaskSuggestionDto> suggestions = List.of(
                new TaskSuggestionDto("Настроить проект", "Инициализировать Spring Boot проект", "HIGH", "PLANING"),
                new TaskSuggestionDto("Создать модели", "Определить сущности и DTO", "HIGH", "PLANING"),
                new TaskSuggestionDto("Реализовать контроллеры", "Написать REST эндпоинты", "INTERMEDIATE", "PLANING")
        );
        given(geminiService.decompose(goal)).willReturn(suggestions);

        mockMvc.perform(post("/ai/decompose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(suggestions)));
    }

    @Test
    void decompose_EmptyGoal_ReturnsEmptyList() throws Exception
    {
        String goal = "";
        DecomposeRequest request = new DecomposeRequest(goal, null);
        given(geminiService.decompose(goal)).willReturn(List.of());

        mockMvc.perform(post("/ai/decompose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void decompose_SingleSuggestion() throws Exception
    {
        String goal = "Написать тесты";
        DecomposeRequest request = new DecomposeRequest(goal, UUID.randomUUID());
        List<TaskSuggestionDto> suggestions = List.of(
                new TaskSuggestionDto("Написать unit тесты", "Покрыть сервисы unit тестами", "HIGH", "PLANING")
        );
        given(geminiService.decompose(goal)).willReturn(suggestions);

        mockMvc.perform(post("/ai/decompose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Написать unit тесты"))
                .andExpect(jsonPath("$[0].importance").value("HIGH"))
                .andExpect(jsonPath("$[0].taskStatus").value("PLANING"));
    }
}
