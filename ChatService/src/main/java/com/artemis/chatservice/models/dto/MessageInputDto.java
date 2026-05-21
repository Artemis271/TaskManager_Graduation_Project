package com.artemis.chatservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.artemis.chatservice.enums.ChatAction;
import com.artemis.chatservice.enums.MessageType;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageInputDto(

        UUID messageId,

        Long ownerId,

        Long userId,

        MessageType type,

        ChatAction action,

        String content,

        UUID chatId

) implements Serializable {}