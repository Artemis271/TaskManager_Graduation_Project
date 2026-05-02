package com.yuranium.taskservice.util.response;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


public record NotFoundResponse(
        int code,

        HttpStatus status,

        LocalDateTime timestamp,

        String message

) implements Serializable {}