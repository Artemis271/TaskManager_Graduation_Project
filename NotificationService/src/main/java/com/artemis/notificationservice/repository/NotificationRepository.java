package com.artemis.notificationservice.repository;

import com.artemis.notificationservice.document.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationDocument, String>
{
    List<NotificationDocument> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsRead(Long userId, boolean isRead);
}
