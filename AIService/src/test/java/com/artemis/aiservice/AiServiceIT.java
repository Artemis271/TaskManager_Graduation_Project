package com.artemis.aiservice;

import com.artemis.aiservice.dto.DecomposeRequest;
import com.artemis.aiservice.dto.TaskSuggestionDto;
import com.artemis.aiservice.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class AiServiceIT {

    @MockBean
    GeminiService geminiService;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void decompose_returnsSuggestionsFromService() {
        List<TaskSuggestionDto> suggestions = List.of(
                new TaskSuggestionDto("Design DB schema", "Create tables and indexes", "HIGH", "PLANING"),
                new TaskSuggestionDto("Write tests", "Cover main use cases", "INTERMEDIATE", "PLANING")
        );
        when(geminiService.decompose(anyString())).thenReturn(suggestions);

        DecomposeRequest request = new DecomposeRequest("Build a task manager", UUID.randomUUID());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<TaskSuggestionDto>> response = restTemplate.exchange(
                "/ai/decompose",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).name()).isEqualTo("Design DB schema");
    }

    @Test
    void decompose_emptyGoal_returnsEmptyList() {
        when(geminiService.decompose(anyString())).thenReturn(List.of());

        DecomposeRequest request = new DecomposeRequest("", UUID.randomUUID());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<TaskSuggestionDto>> response = restTemplate.exchange(
                "/ai/decompose",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
