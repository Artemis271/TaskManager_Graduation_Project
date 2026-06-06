package com.artemis.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.artemis.taskservice.enums.TaskImportance;
import com.artemis.taskservice.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.artemis.taskservice.entity.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskUpdateDto(
        String name,

        String description,

        TaskImportance taskImportance,

        TaskStatus taskStatus,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateFinished,

        Boolean isFinished

) implements Serializable {}