package com.artemis.chatservice.service;

import com.artemis.chatservice.models.document.ChatDocument;
import com.artemis.chatservice.models.dto.ResponseMessage;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChatService
{
    ChatDocument createChat(String title, Long ownerId);

    List<ChatDocument> getAllChats(Long userId, Pageable pageable);

    ResponseMessage addUserToChat(UUID chatId, Long userId);

    ResponseMessage deleteUserFromChat(UUID chatId, Long userId);

    ResponseMessage deleteChat(UUID chatId, Long ownerId);
}