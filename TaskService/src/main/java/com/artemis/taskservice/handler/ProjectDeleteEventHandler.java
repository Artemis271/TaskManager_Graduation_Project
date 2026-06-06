package com.artemis.taskservice.handler;

import com.artemis.taskservice.entity.ProcessedEventEntity;
import com.artemis.taskservice.repository.ProcessedRepository;
import com.artemis.taskservice.sevice.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectDeleteEventHandler
{
    private final TaskService taskService;

    private final ProcessedRepository processedRepository;

    @Transactional
    @KafkaListener(topics = "project-deleted-events-topic", groupId = "task-project-delete")
    public void deleteProject(@Payload UUID projectId,
                              @Header("messageId") String messageId)
    {
        if (processedRepository.findByMessageId(UUID.fromString(messageId)).isPresent())
            return;

        taskService.deleteAllTask(projectId);
        processedRepository.save(
                new ProcessedEventEntity(UUID.fromString(messageId), projectId));
    }

    @Transactional
    @KafkaListener(topics = "projects-deleted-events-topic", groupId = "task-projects-delete")
    public void deleteProjects(@Payload List<UUID> projectsId,
                               @Header("messageId") String messageId)
    {
        if (processedRepository.findByMessageId(
                UUID.fromString(messageId)).isPresent() || projectsId.isEmpty())
            return;

        taskService.deleteAllTask(projectsId);
        processedRepository.save(
                new ProcessedEventEntity(UUID.fromString(messageId),
                        projectsId.get(0)
                ));
    }
}