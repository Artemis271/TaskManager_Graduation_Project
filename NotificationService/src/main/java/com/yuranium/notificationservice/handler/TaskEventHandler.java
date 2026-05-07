package com.yuranium.notificationservice.handler;

import com.yuranium.core.events.TaskCreatedEvent;
import com.yuranium.core.events.TaskStatusChangedEvent;
import com.yuranium.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventHandler
{
    private final NotificationService notificationService;

    @KafkaListener(topics = "task-created-events-topic", groupId = "notification-task-create")
    public void handleTaskCreated(@Payload TaskCreatedEvent event)
    {
        if (event.assigneeId() == null) return;

        notificationService.createAndSend(
                event.assigneeId(),
                "Новая задача",
                String.format("Вам назначена задача: «%s»", event.taskName())
        );
    }

    @KafkaListener(topics = "task-status-changed-events-topic", groupId = "notification-task-status")
    public void handleTaskStatusChanged(@Payload TaskStatusChangedEvent event)
    {
        if (event.assigneeId() == null) return;

        notificationService.createAndSend(
                event.assigneeId(),
                "Статус задачи изменён",
                String.format("Задача «%s»: статус изменён с %s на %s",
                        event.taskName(), event.oldStatus(), event.newStatus())
        );
    }
}
