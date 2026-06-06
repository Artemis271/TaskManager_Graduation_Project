package com.artemis.taskservice;

import com.artemis.taskservice.dto.TaskDto;
import com.artemis.taskservice.enums.TaskImportance;
import com.artemis.taskservice.enums.TaskStatus;
import com.artemis.taskservice.sevice.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class TaskServiceIT {

    @MockBean
    KafkaProducer kafkaProducer;

    @Autowired
    TestRestTemplate restTemplate;

    private HttpEntity<MultiValueMap<String, Object>> buildTaskRequest(
            String name, UUID projectId, Long userId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("description", "Test description for " + name);
        body.add("taskImportance", TaskImportance.INTERMEDIATE.name());
        body.add("taskStatus", TaskStatus.PLANING.name());
        body.add("projectId", projectId.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-User-Id", String.valueOf(userId));
        return new HttpEntity<>(body, headers);
    }

    @Test
    void createTask_success_returns201() {
        UUID projectId = UUID.randomUUID();
        ResponseEntity<TaskDto> response = restTemplate.postForEntity(
                "/tasks/createTask",
                buildTaskRequest("My First Task", projectId, 1L),
                TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("My First Task");
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void getAllTasks_byProjectId_returnsTasks() {
        UUID projectId = UUID.randomUUID();
        restTemplate.postForEntity("/tasks/createTask",
                buildTaskRequest("Task Alpha", projectId, 2L), TaskDto.class);
        restTemplate.postForEntity("/tasks/createTask",
                buildTaskRequest("Task Beta", projectId, 2L), TaskDto.class);

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/tasks/allTasks?projectId=" + projectId, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void getTask_byId_returnsTask() {
        UUID projectId = UUID.randomUUID();
        ResponseEntity<TaskDto> created = restTemplate.postForEntity(
                "/tasks/createTask",
                buildTaskRequest("Findable Task", projectId, 3L),
                TaskDto.class);
        UUID taskId = created.getBody().id();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "3");
        ResponseEntity<TaskDto> response = restTemplate.exchange(
                "/tasks/" + taskId, HttpMethod.GET,
                new HttpEntity<>(headers), TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Findable Task");
    }

    @Test
    void deleteTask_success_returns204() {
        UUID projectId = UUID.randomUUID();
        ResponseEntity<TaskDto> created = restTemplate.postForEntity(
                "/tasks/createTask",
                buildTaskRequest("Deletable Task", projectId, 4L),
                TaskDto.class);
        UUID taskId = created.getBody().id();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "4");
        ResponseEntity<Void> response = restTemplate.exchange(
                "/tasks/delete/" + taskId, HttpMethod.DELETE,
                new HttpEntity<>(headers), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getAllTaskImportance_returnsAllValues() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/tasks/allTaskImportance", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
