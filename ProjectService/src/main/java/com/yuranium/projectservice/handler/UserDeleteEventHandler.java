package com.yuranium.projectservice.handler;

import com.yuranium.projectservice.entity.ProcessedEventEntity;
import com.yuranium.projectservice.entity.ProjectEntity;
import com.yuranium.projectservice.repository.ProcessedRepository;
import com.yuranium.projectservice.service.KafkaProducer;
import com.yuranium.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDeleteEventHandler
{
    private final ProjectService projectService;

    private final ProcessedRepository processedRepository;

    private final KafkaProducer kafkaProducer;

    @Transactional
    @KafkaListener(topics = "user-deleted-events-topic", groupId = "project-user-delete")
    public void deleteProject(@Payload Long userId,
                              @Header("messageId") String messageId)
    {
        if (processedRepository.findByMessageId(UUID.fromString(messageId)).isPresent())
            return;

        projectService.deleteAllProject(userId);
        kafkaProducer.sendDeleteProjectsEvent(
                projectService.getAllByUserId(userId).stream()
                        .map(ProjectEntity::getId)
                        .toList()
        );
        processedRepository.save(
                new ProcessedEventEntity(UUID.fromString(messageId),
                        userId));
    }
}