package com.artemis.taskservice.mapper;

import com.artemis.taskservice.dto.TaskChartDto;
import com.artemis.taskservice.dto.TaskDto;
import com.artemis.taskservice.dto.TaskInputDto;
import com.artemis.taskservice.dto.TaskUpdateDto;
import com.artemis.taskservice.entity.TaskEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TaskImageMapper.class
)
public interface TaskMapper
{
    TaskEntity toEntity(TaskDto taskDto);

    List<TaskDto> toDto(List<TaskEntity> taskEntity);

    TaskDto toDto(TaskEntity taskEntity);

    List<TaskEntity> toEntity(List<TaskDto> taskDto);

    TaskEntity toEntity(TaskUpdateDto updatedDto);

    @Mapping(target = "images", ignore = true)
    TaskEntity toEntity(TaskInputDto taskInputDto);

    TaskChartDto toChartDto(TaskEntity taskEntity);

    List<TaskChartDto> toChartDto(List<TaskEntity> taskEntities);
}