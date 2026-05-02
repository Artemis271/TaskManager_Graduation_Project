package com.yuranium.authservice.service.kafka;

import com.yuranium.authservice.models.entity.AvatarEntity;
import com.yuranium.authservice.models.entity.UserEntity;
import com.yuranium.authservice.service.AvatarService;
import com.yuranium.core.events.UserCreatedEvent;
import com.yuranium.core.events.UserUpdatedEvent;
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

    private final AvatarService avatarService;

    public void sendDeleteUserEvent(Long userId)
    {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                environment.getProperty("kafka.topic-names.user-delete"), userId);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

    public void sendCreateUserEvent(UserEntity user)
    {
        final byte[] avatarData = user.getAvatars().isEmpty() ? null :
                avatarService.compressImage(
                        user.getAvatars().get(0).getBinaryData()
                );

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                environment.getProperty("kafka.topic-names.user-create"),
                new UserCreatedEvent(user.getId(), user.getUsername(), avatarData));

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

    public void sendUpdateUserEvent(UserEntity user)
    {
        byte[] avatarData = null;
        if (!user.getAvatars().isEmpty())
        {
            AvatarEntity last = user.getAvatars()
                    .get(user.getAvatars().size() - 1);
            avatarData = avatarService.compressImage(last.getBinaryData());
        }

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                environment.getProperty("kafka.topic-names.user-update"),
                new UserUpdatedEvent(user.getId(), user.getUsername(),
                        avatarData));

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }
}