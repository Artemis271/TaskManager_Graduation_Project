package com.yuranium.chatservice.controller;

import com.yuranium.chatservice.models.document.ChatDocument;
import com.yuranium.chatservice.models.document.UserDocument;
import com.yuranium.chatservice.models.dto.OutputMessage;
import com.yuranium.chatservice.service.ChatServiceImpl;
import com.yuranium.chatservice.service.MessageService;
import com.yuranium.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController
{
    private final UserService userService;

    private final ChatServiceImpl chatService;

    private final MessageService messageService;

    @GetMapping("/user/search")
    public ResponseEntity<List<UserDocument>> searchUser(
            @RequestParam String inputUsername,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "50") int size)
    {
        return new ResponseEntity<>(
                userService.searchUser(inputUsername,
                        PageRequest.of(pageNumber, size)),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/my-team")
    public ResponseEntity<List<UserDocument>> getMyTeam(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "50") int size)
    {
        return new ResponseEntity<>(
                userService.myTeam(userId,
                        PageRequest.of(pageNumber, size)),
                HttpStatus.OK
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatDocument>> getAllChats(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "50") int size)
    {
        return new ResponseEntity<>(
                chatService.getAllChats(userId, PageRequest.of(
                pageNumber, size, Sort.by("dateCreated"))),
                HttpStatus.OK
        );
    }

    @GetMapping("/messages/all")
    public ResponseEntity<List<OutputMessage>> getAllMessages(
            @RequestParam UUID chatId,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "50") int size)
    {
        return new ResponseEntity<>(
                messageService.getAllMessages(chatId,
                PageRequest.of(pageNumber, size)), HttpStatus.OK
        );
    }

    @PostMapping("/create-chat")
    public ResponseEntity<ChatDocument> createChat(@RequestParam String chatTitle,
                                                   @RequestParam Long ownerId)
    {
        return new ResponseEntity<>(
                chatService.createChat(chatTitle, ownerId),
                HttpStatus.CREATED
        );
    }
}