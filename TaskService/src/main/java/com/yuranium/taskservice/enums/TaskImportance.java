package com.yuranium.taskservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskImportance
{
    LOW("Не важная"), INTERMEDIATE("Обычная"), HIGH("Высокая");

    private final String importance;
}