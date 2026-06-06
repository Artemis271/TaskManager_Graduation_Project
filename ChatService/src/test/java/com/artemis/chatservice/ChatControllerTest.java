package com.artemis.chatservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.artemis.chatservice.controller.ChatController;
import com.artemis.chatservice.models.document.ChatDocument;
import com.artemis.chatservice.models.document.UserDocument;
import com.artemis.chatservice.models.dto.OutputMessage;
import com.artemis.chatservice.service.ChatServiceImpl;
import com.artemis.chatservice.service.MessageService;
import com.artemis.chatservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChatControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ChatServiceImpl chatService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatController chatController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void searchUser_DefaultPaging() throws Exception
    {
        UserDocument user = UserDocument.builder()
                .id(1L).username("john").build();
        given(userService.searchUser(eq("john"), any()))
                .willReturn(List.of(user));

        mockMvc.perform(get("/chat/user/search")
                        .param("inputUsername", "john"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    @Test
    void searchUser_CustomPaging() throws Exception
    {
        UserDocument user = UserDocument.builder()
                .id(2L).username("alice").build();
        given(userService.searchUser(eq("alice"), eq(PageRequest.of(1, 10))))
                .willReturn(List.of(user));

        mockMvc.perform(get("/chat/user/search")
                        .param("inputUsername", "alice")
                        .param("pageNumber", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    @Test
    void getMyTeam_DefaultPaging() throws Exception
    {
        UserDocument teammate = UserDocument.builder()
                .id(5L).username("bob").build();
        given(userService.myTeam(eq(42L), any()))
                .willReturn(List.of(teammate));

        mockMvc.perform(get("/chat/user/my-team")
                        .param("userId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(teammate))));
    }

    @Test
    void getAllChats_DefaultPaging() throws Exception
    {
        ChatDocument chat = ChatDocument.builder()
                .id(UUID.randomUUID())
                .title("General")
                .dateCreated(LocalDateTime.now())
                .ownerId(1L)
                .userIds(List.of(1L, 2L))
                .build();
        given(chatService.getAllChats(eq(1L), any()))
                .willReturn(List.of(chat));

        mockMvc.perform(get("/chat/all")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(chat))));
    }

    @Test
    void getAllChats_CustomPaging() throws Exception
    {
        given(chatService.getAllChats(eq(7L),
                eq(PageRequest.of(1, 10, Sort.by("dateCreated")))))
                .willReturn(List.of());

        mockMvc.perform(get("/chat/all")
                        .param("userId", "7")
                        .param("pageNumber", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllMessages() throws Exception
    {
        UUID chatId = UUID.randomUUID();
        OutputMessage msg = new OutputMessage();
        msg.setContent("Hello");
        msg.setChatId(chatId);
        msg.setOwnerId(1L);
        given(messageService.getAllMessages(eq(chatId), any()))
                .willReturn(List.of(msg));

        mockMvc.perform(get("/chat/messages/all")
                        .param("chatId", chatId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(msg))));
    }

    @Test
    void createChat() throws Exception
    {
        ChatDocument created = ChatDocument.builder()
                .id(UUID.randomUUID())
                .title("My Chat")
                .dateCreated(LocalDateTime.now())
                .ownerId(99L)
                .userIds(List.of(99L))
                .build();
        given(chatService.createChat(eq("My Chat"), eq(99L)))
                .willReturn(created);

        mockMvc.perform(post("/chat/create-chat")
                        .param("chatTitle", "My Chat")
                        .param("ownerId", "99")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }
}
