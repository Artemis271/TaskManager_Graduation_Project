package com.yuranium.chatservice.service;

import com.mongodb.client.result.DeleteResult;
import com.yuranium.chatservice.models.document.MessageDocument;
import com.yuranium.chatservice.models.document.UserDocument;
import com.yuranium.chatservice.models.dto.MessageInputDto;
import com.yuranium.chatservice.models.dto.OutputMessage;
import com.yuranium.chatservice.models.dto.ResponseMessage;
import com.yuranium.chatservice.util.exception.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService
{
    private final MongoTemplate mongoTemplate;

    public List<OutputMessage> getAllMessages(UUID chatId, Pageable pageable)
    {
        if (chatId == null)
            throw new IllegalArgumentException(
                    "The chatId is null, cannot load the messages"
            );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatId").is(chatId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "dateCreated")),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.lookup(
                        "users",
                        "ownerId",
                        "_id",
                        "userInfo"
                ),
                Aggregation.unwind("userInfo"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "dateCreated")),
                Aggregation.project()
                        .andInclude("id", "dateCreated", "content", "chatId")
                        .and("ownerId").as("ownerId")
                        .and("messageType").as("type")
                        .and("userInfo.username").as("username")
                        .and("userInfo.avatarData").as("avatarData")
        );
        return mongoTemplate
                .aggregate(aggregation, "messages", OutputMessage.class)
                .getMappedResults();
    }

    public ResponseMessage insertMessage(MessageInputDto message)
    {
        UserDocument user = mongoTemplate.findById(message.ownerId(), UserDocument.class);
        MessageDocument result = mongoTemplate.insert(MessageDocument.builder()
                .id(UUID.randomUUID())
                .type(message.type())
                .dateCreated(LocalDateTime.now())
                .ownerId(message.ownerId())
                .content(message.content())
                .chatId(message.chatId())
                .build());
        OutputMessage outputMessage = new OutputMessage(result);
        outputMessage.setUsername(user.getUsername());
        outputMessage.setAvatarData(user.getBinaryData());
        return outputMessage;
    }

    public MessageDocument deleteMessage(UUID messageId)
    {
        MessageDocument message = mongoTemplate.findById(messageId, MessageDocument.class);
        DeleteResult result = mongoTemplate.remove(message);

        if (result.getDeletedCount() == 0)
            throw new MessageNotFoundException(
                String.format("The message with id=%s was not found",
                        messageId)
        );

        return MessageDocument.builder()
                .dateCreated(LocalDateTime.now())
                .content(String.format(
                        "Сообщение '%s' удалено",
                        message.getContent())
                )
                .build();
    }
}