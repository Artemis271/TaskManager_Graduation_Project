package com.artemis.projectservice;

import com.artemis.projectservice.dto.ProjectDto;
import com.artemis.projectservice.service.KafkaProducer;
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
class ProjectServiceIT {

    @MockBean
    KafkaProducer kafkaProducer;

    @Autowired
    TestRestTemplate restTemplate;

    private HttpEntity<MultiValueMap<String, Object>> buildProjectRequest(String name, String description, Long userId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("description", description);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-User-Id", String.valueOf(userId));
        return new HttpEntity<>(body, headers);
    }

    @Test
    void createProject_success_returns201() {
        ResponseEntity<ProjectDto> response = restTemplate.postForEntity(
                "/projects/createProject",
                buildProjectRequest("Test Project", "Test description", 1L),
                ProjectDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test Project");
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void getAllProjects_returnsProjectsForUser() {
        restTemplate.postForEntity("/projects/createProject",
                buildProjectRequest("Project A", "Desc A", 10L), ProjectDto.class);
        restTemplate.postForEntity("/projects/createProject",
                buildProjectRequest("Project B", "Desc B", 10L), ProjectDto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "10");
        ResponseEntity<List> response = restTemplate.exchange(
                "/projects/allProjects", HttpMethod.GET,
                new HttpEntity<>(headers), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void getProject_byId_returnsProject() {
        ResponseEntity<ProjectDto> created = restTemplate.postForEntity(
                "/projects/createProject",
                buildProjectRequest("Findable Project", "Find me", 2L),
                ProjectDto.class);
        UUID projectId = created.getBody().id();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "2");
        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                "/projects/" + projectId, HttpMethod.GET,
                new HttpEntity<>(headers), ProjectDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Findable Project");
    }

    @Test
    void deleteProject_success_returns204() {
        ResponseEntity<ProjectDto> created = restTemplate.postForEntity(
                "/projects/createProject",
                buildProjectRequest("Delete Me", "To be deleted", 3L),
                ProjectDto.class);
        UUID projectId = created.getBody().id();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "3");
        ResponseEntity<Void> response = restTemplate.exchange(
                "/projects/delete/" + projectId, HttpMethod.DELETE,
                new HttpEntity<>(headers), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
