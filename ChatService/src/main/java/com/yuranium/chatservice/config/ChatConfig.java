package com.yuranium.chatservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuranium.chatservice.enums.ChatAction;
import com.yuranium.chatservice.models.dto.MessageInputDto;
import com.yuranium.chatservice.models.dto.ResponseMessage;
import com.yuranium.chatservice.service.ChatService;
import com.yuranium.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.EnumMap;
import java.util.function.Function;

@Configuration
@EnableCaching
@EnableMongoAuditing
@RequiredArgsConstructor
public class ChatConfig
{
    private final MessageService messageService;

    private final ChatService chatService;

    @Bean
    EnumMap<ChatAction, Function<MessageInputDto, ResponseMessage>> chatHandlers()
    {
        EnumMap<ChatAction, Function<MessageInputDto, ResponseMessage>> handlers = new EnumMap<>(ChatAction.class);

        handlers.put(ChatAction.NEW_MESSAGE, messageService::insertMessage);
        handlers.put(ChatAction.DELETE_MESSAGE, message -> messageService.deleteMessage(message.messageId()));
        handlers.put(ChatAction.ADD_USER, message -> chatService.addUserToChat(message.chatId(), message.userId()));
        handlers.put(ChatAction.DELETE_USER, message -> chatService.deleteUserFromChat(message.chatId(), message.userId()));
        handlers.put(ChatAction.DELETE_CHAT, message -> chatService.deleteChat(message.chatId(), message.ownerId()));

        return handlers;
    }

    @Bean
    public RedisCacheConfiguration cacheConfig()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
}