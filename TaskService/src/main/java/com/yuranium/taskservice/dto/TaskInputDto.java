package com.yuranium.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.yuranium.taskservice.entity.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskInputDto(
        String name,

        String description,

        TaskImportance taskImportance,

        TaskStatus taskStatus,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd", timezone = "UTC")
        LocalDate dateFinished,

        List<MultipartFile> images,

        UUID projectId

) implements Serializable {}