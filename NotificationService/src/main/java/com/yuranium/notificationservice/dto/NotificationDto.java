package com.yuranium.notificationservice.dto;

import java.time.LocalDateTime;

public record NotificationDto(

        String id,

        Long userId,

        String title,

        String message,

        boolean isRead,

        LocalDateTime createdAt

) {}
