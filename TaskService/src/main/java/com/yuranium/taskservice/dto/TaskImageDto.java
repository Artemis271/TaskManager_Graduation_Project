package com.yuranium.taskservice.dto;


import java.io.Serializable;
import java.time.LocalDateTime;

public record TaskImageDto(
        Long id,

        String name,

        String contentType,

        byte[] binaryData,

        LocalDateTime dateAdded

) implements Serializable {}