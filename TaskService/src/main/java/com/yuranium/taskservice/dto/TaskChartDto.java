package com.yuranium.taskservice.dto;

import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record TaskChartDto(

        UUID id,

        String name,

        TaskStatus taskStatus,

        TaskImportance taskImportance,

        LocalDate dateAdded,

        UUID projectId

) implements Serializable {}