package com.artemis.chatservice;

import com.artemis.chatservice.models.document.ChatDocument;
import com.artemis.chatservice.models.document.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class ChatServiceIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void clearCollections() {
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(ChatDocument.class);
    }

    @Test
    void createChat_success_returns201() {
        ResponseEntity<ChatDocument> response = restTemplate.postForEntity(
                "/chat/create-chat?chatTitle=TestRoom&ownerId=1",
                null, ChatDocument.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("TestRoom");
        assertThat(response.getBody().getOwnerId()).isEqualTo(1L);
    }

    @Test
    void getAllChats_afterCreate_returnsChats() {
        restTemplate.postForEntity("/chat/create-chat?chatTitle=Room1&ownerId=42", null, ChatDocument.class);
        restTemplate.postForEntity("/chat/create-chat?chatTitle=Room2&ownerId=42", null, ChatDocument.class);

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/chat/all?userId=42", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void searchUser_findsExistingUser() {
        mongoTemplate.insert(UserDocument.builder()
                .id(100L)
                .username("johndoe")
                .build());

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/chat/user/search?inputUsername=john", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAllMessages_forNewChat_returnsEmpty() {
        ResponseEntity<ChatDocument> created = restTemplate.postForEntity(
                "/chat/create-chat?chatTitle=EmptyRoom&ownerId=5",
                null, ChatDocument.class);
        UUID chatId = created.getBody().getId();

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/chat/messages/all?chatId=" + chatId, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
