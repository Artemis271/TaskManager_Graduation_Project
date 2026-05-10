package com.yuranium.aiservice.dto;

public record TaskSuggestionDto(
        String name,
        String description,
        String importance,
        String taskStatus
) {}
