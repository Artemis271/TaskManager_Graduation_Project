package com.artemis.taskservice.mapper;

import com.artemis.taskservice.dto.TaskImageDto;
import com.artemis.taskservice.dto.TaskImageInputDto;
import com.artemis.taskservice.entity.TaskImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskImageMapper
{
    TaskImageEntity toEntity(TaskImageDto avatarDto);

    List<TaskImageDto> toDto(List<TaskImageDto> avatarEntity);

    TaskImageDto toDto(TaskImageEntity avatarEntity);

    List<TaskImageEntity> toEntity(List<TaskImageDto> avatarDto);
}