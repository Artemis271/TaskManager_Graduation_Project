package com.yuranium.projectservice.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record AvatarDto(
        Long id,

        String name,

        String contentType,

        byte[] binaryData,

        LocalDate dateAdded

) implements Serializable {}