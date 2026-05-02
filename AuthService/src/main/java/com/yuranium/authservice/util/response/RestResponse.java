package com.yuranium.authservice.util.response;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RestResponse(

        int code,

        HttpStatus status,

        LocalDateTime timestamp,

        String message

) implements Serializable {}