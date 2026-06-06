package com.artemis.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.artemis.taskservice.enums.TaskImportance;
import com.artemis.taskservice.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskDto(

        UUID id,

        String name,

        String description,

        TaskImportance taskImportance,

        TaskStatus taskStatus,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateAdded,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateFinished,

        Boolean isFinished,

        List<TaskImageDto> images

) implements Serializable {}