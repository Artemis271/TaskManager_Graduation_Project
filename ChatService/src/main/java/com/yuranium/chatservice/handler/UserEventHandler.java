package com.yuranium.chatservice.handler;

import com.yuranium.chatservice.service.UserService;
import com.yuranium.core.events.UserCreatedEvent;
import com.yuranium.core.events.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventHandler
{
    private final UserService userService;

    @KafkaListener(topics = "user-created-events-topic", groupId = "chat-user-create")
    public void createUserEvent(@Payload UserCreatedEvent userEvent,
                                @Header("messageId") String messageId)
    {
        userService.createUser(userEvent);
    }

    @KafkaListener(topics = "user-updated-events-topic", groupId = "chat-user-update")
    public void updateUserEvent(@Payload UserUpdatedEvent userEvent,
                                @Header("messageId") String messageId)
    {
        userService.updateUser(userEvent);
    }

    @KafkaListener(topics = "user-deleted-events-topic", groupId = "chat-user-delete")
    public void deleteUserEvent(@Payload Long userId,
                                @Header("messageId") String messageId)
    {
        userService.deleteUser(userId);
    }
}