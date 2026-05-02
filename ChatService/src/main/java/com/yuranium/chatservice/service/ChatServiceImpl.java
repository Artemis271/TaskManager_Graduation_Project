package com.yuranium.chatservice.service;

import com.yuranium.chatservice.enums.MessageType;
import com.yuranium.chatservice.models.document.ChatDocument;
import com.yuranium.chatservice.models.document.MessageDocument;
import com.yuranium.chatservice.models.document.UserDocument;
import com.yuranium.chatservice.models.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "chat")
public class ChatServiceImpl implements ChatService
{
    private final MongoTemplate mongoTemplate;

    public ChatDocument createChat(String title, Long ownerId)
    {
        return mongoTemplate.insert(ChatDocument.builder()
                .id(UUID.randomUUID())
                .title(title)
                .dateCreated(LocalDateTime.now())
                .ownerId(ownerId)
                .userIds(List.of(ownerId))
                .build()
        );
    }

    @Cacheable(key = "#userId", unless = "#result.size() == 0")
    public List<ChatDocument> getAllChats(Long userId, Pageable pageable)
    {
       return mongoTemplate.find(
               Query.query(Criteria.where("userIds")
                               .elemMatch(Criteria.where("$eq").is(userId)))
                       .with(pageable), ChatDocument.class);
    }


    @CacheEvict(key = "#chatId")
    public ResponseMessage addUserToChat(UUID chatId, Long userId)
    {
       ChatDocument chatDocument = mongoTemplate.findById(chatId, ChatDocument.class);
       if (chatDocument.getUserIds().contains(userId))
           throw new UnsupportedOperationException(
                   String.format("The user with id=%d already exists in this chat", userId)
           );
       mongoTemplate.updateFirst(
               Query.query(Criteria.where("_id").is(chatId)),
               new Update().addToSet("userIds", userId),
               ChatDocument.class
       );
       UserDocument user = mongoTemplate.findById(userId, UserDocument.class);
       return mongoTemplate.save(MessageDocument.builder()
               .id(UUID.randomUUID())
               .type(MessageType.JOIN)
               .dateCreated(LocalDateTime.now())
               .ownerId(userId)
               .content(String.format(
                       "Пользователь '%s' присоединился к чату!",
                       user.getUsername()
               ))
               .chatId(chatId)
               .build());
    }

    @CacheEvict(key = "#chatId")
    public ResponseMessage deleteUserFromChat(UUID chatId, Long userId)
    {
        ChatDocument chatDocument = mongoTemplate.findById(chatId, ChatDocument.class);

        if (!chatDocument.getUserIds().contains(userId))
            throw new UnsupportedOperationException(
                    String.format(
                            "The user with id=%d cannot be deleted, because he is absent",
                            userId
                    )
            );
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(chatId)),
                new Update().pull("userIds", userId),
                ChatDocument.class
        );
        UserDocument user = mongoTemplate.findById(userId, UserDocument.class);
        return mongoTemplate.save(MessageDocument.builder()
                .id(UUID.randomUUID())
                .type(MessageType.LEAVE)
                .dateCreated(LocalDateTime.now())
                .ownerId(userId)
                .content(String.format(
                        "Пользователь '%s' покинул чат",
                        user.getUsername()
                ))
                .chatId(chatId)
                .build());
    }

    @Caching(evict = {
            @CacheEvict(key = "#ownerId"),
            @CacheEvict(key = "#chatId")
    })
    public ResponseMessage deleteChat(UUID chatId, Long ownerId)
    {
        ChatDocument chat = mongoTemplate.findById(chatId, ChatDocument.class);
        if (chat.getOwnerId().longValue() == ownerId.longValue())
            mongoTemplate.remove(chat);
        else throw new UnsupportedOperationException(
                String.format(
                        "The user with id=%d is not a owner of this chat",
                        ownerId
                )
        );

        return MessageDocument.builder()
                .dateCreated(LocalDateTime.now())
                .content(String.format("Чат '%s' был удалён", chat.getTitle()))
                .chatId(chatId)
                .build();
    }
}