package com.yuranium.projectservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducer
{
    @Value("${kafka.topic-names.projects-delete}")
    private String projectsDeleteTopicName;

    @Value("${kafka.topic-names.project-delete}")
    private String projectDeleteTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDeleteProjectsEvent(List<UUID> projectsId)
    {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(projectsDeleteTopicName, projectsId);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

    public void sendDeleteProjectEvent(UUID projectId)
    {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(projectDeleteTopicName, projectId);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }
}