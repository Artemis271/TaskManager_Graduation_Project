package com.artemis.authservice;

import com.artemis.authservice.models.dto.UserDto;
import com.artemis.authservice.models.dto.UserInfoDto;
import com.artemis.authservice.service.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class AuthServiceIT {

    @MockBean
    KafkaProducer kafkaProducer;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void register_success_thenLogin_returnsToken() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("username", "integrationuser");
        body.add("password", "Password123!");
        body.add("email", "integration@test.com");
        body.add("name", "Integration");
        body.add("lastName", "Test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<UserDto> registerResponse = restTemplate.postForEntity(
                "/auth/registration", new HttpEntity<>(body, headers), UserDto.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().email()).isEqualTo("integration@test.com");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        String loginJson = "{\"username\":\"integration@test.com\",\"password\":\"Password123!\"}";

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "/auth/login", new HttpEntity<>(loginJson, loginHeaders), Map.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).containsKey("token");
    }

    @Test
    void getUser_withValidJwt_returnsUserInfo() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("username", "jwtuser");
        body.add("password", "Secret456!");
        body.add("email", "jwtuser@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<UserDto> registerResponse = restTemplate.postForEntity(
                "/auth/registration", new HttpEntity<>(body, headers), UserDto.class);
        Long userId = registerResponse.getBody().id();

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        String loginJson = "{\"username\":\"jwtuser@test.com\",\"password\":\"Secret456!\"}";
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "/auth/login", new HttpEntity<>(loginJson, loginHeaders), Map.class);
        String token = (String) loginResponse.getBody().get("token");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        ResponseEntity<UserInfoDto> getResponse = restTemplate.exchange(
                "/auth/user/" + userId, HttpMethod.GET,
                new HttpEntity<>(authHeaders), UserInfoDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().email()).isEqualTo("jwtuser@test.com");
    }

    @Test
    void register_duplicateEmail_returns4xx() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("username", "dupuser");
        body.add("password", "Pass789!");
        body.add("email", "duplicate@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity("/auth/registration", request, UserDto.class);
        ResponseEntity<String> second = restTemplate.postForEntity(
                "/auth/registration", request, String.class);

        assertThat(second.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void login_wrongCredentials_returns4xx() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginJson = "{\"username\":\"nonexistent@test.com\",\"password\":\"WrongPass\"}";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/login", new HttpEntity<>(loginJson, headers), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
