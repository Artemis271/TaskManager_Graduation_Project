package com.yuranium.chatservice.controller;

import com.yuranium.chatservice.enums.ChatAction;
import com.yuranium.chatservice.models.dto.MessageInputDto;
import com.yuranium.chatservice.models.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.EnumMap;
import java.util.function.Function;

@Controller
@RequiredArgsConstructor
public class WebSocketController
{
    private final SimpMessagingTemplate template;

    private final EnumMap<ChatAction, Function<MessageInputDto, ResponseMessage>> chatHandlers;

    @MessageMapping("/chat/send-message")
    public void processMessage(@Payload MessageInputDto message)
    {
        ResponseMessage payload = chatHandlers
                .getOrDefault(message.action(), msg -> null)
                .apply(message);

        template.convertAndSend(
                String.format("/topic/chats/%s/new-message",
                        message.chatId()),
                payload);
    }
}