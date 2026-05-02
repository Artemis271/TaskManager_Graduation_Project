package com.yuranium.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectDto(
        UUID id,

        String name,

        String description,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateAdded,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateUpdated,

        List<AvatarDto> avatars

) implements Serializable {}