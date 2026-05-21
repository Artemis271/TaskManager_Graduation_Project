package com.artemis.notificationservice.service;

import com.artemis.notificationservice.document.NotificationDocument;
import com.artemis.notificationservice.document.UserDocument;
import com.artemis.notificationservice.dto.NotificationDto;
import com.artemis.notificationservice.repository.NotificationRepository;
import com.artemis.notificationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService
{
    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final SimpMessagingTemplate messagingTemplate;

    public void createAndSend(Long userId, String title, String message)
    {
        NotificationDocument notification = new NotificationDocument();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);

        NotificationDto dto = toDto(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, dto);

        userRepository.findById(userId).ifPresent(user ->
                emailService.send(user.getEmail(), title, message));
    }

    public List<NotificationDto> getAll(Long userId)
    {
        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public long getUnreadCount(Long userId)
    {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    public void markAsRead(String notificationId)
    {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long userId)
    {
        List<NotificationDocument> unread = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(n -> !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void saveUser(Long id, String username, String email)
    {
        userRepository.save(new UserDocument(id, username, email));
    }

    private NotificationDto toDto(NotificationDocument doc)
    {
        return new NotificationDto(doc.getId(), doc.getUserId(),
                doc.getTitle(), doc.getMessage(),
                doc.isRead(), doc.getCreatedAt());
    }
}
