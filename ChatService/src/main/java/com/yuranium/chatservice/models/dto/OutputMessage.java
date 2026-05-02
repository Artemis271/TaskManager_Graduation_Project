package com.yuranium.chatservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.chatservice.enums.MessageType;
import com.yuranium.chatservice.models.document.MessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputMessage implements Serializable, ResponseMessage
{
    private UUID id;

    private MessageType type;

    private LocalDateTime dateCreated;

    private Long ownerId;

    private String username;

    private byte[] avatarData;

    private String content;

    private UUID chatId;

    public OutputMessage(MessageDocument messageDocument)
    {
        this.id = messageDocument.getId();
        this.type = messageDocument.getType();
        this.dateCreated = messageDocument.getDateCreated();
        this.ownerId = messageDocument.getOwnerId();
        this.content = messageDocument.getContent();
        this.chatId = messageDocument.getChatId();
    }
}