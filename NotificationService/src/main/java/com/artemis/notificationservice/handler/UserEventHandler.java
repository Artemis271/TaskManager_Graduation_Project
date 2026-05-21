package com.artemis.notificationservice.handler;

import com.artemis.core.events.UserCreatedEvent;
import com.artemis.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventHandler
{
    private final NotificationService notificationService;

    @KafkaListener(topics = "user-created-events-topic", groupId = "notification-user-create")
    public void handleUserCreated(@Payload UserCreatedEvent event)
    {
        notificationService.saveUser(event.id(), event.username(), event.email());
    }
}
