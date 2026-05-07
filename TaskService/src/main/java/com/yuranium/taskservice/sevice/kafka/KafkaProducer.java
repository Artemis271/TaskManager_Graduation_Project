package com.yuranium.taskservice.sevice.kafka;

import com.yuranium.core.events.TaskCreatedEvent;
import com.yuranium.core.events.TaskStatusChangedEvent;
import com.yuranium.taskservice.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducer
{
    private final Environment environment;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTaskCreatedEvent(TaskEntity task)
    {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                environment.getProperty("kafka.topic-names.task-create"),
                new TaskCreatedEvent(task.getId(), task.getName(),
                        task.getProjectId(), task.getAssigneeId()));

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

    public void sendTaskStatusChangedEvent(TaskEntity task, String oldStatus)
    {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                environment.getProperty("kafka.topic-names.task-status-changed"),
                new TaskStatusChangedEvent(task.getId(), task.getName(),
                        oldStatus, task.getTaskStatus().name(),
                        task.getProjectId(), task.getAssigneeId()));

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }
}
