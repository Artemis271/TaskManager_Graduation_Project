package com.artemis.taskservice.dto;

import com.artemis.taskservice.enums.TaskImportance;
import com.artemis.taskservice.enums.TaskStatus;

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