package com.yuranium.taskservice.mapper;

import com.yuranium.taskservice.dto.TaskImageDto;
import com.yuranium.taskservice.dto.TaskImageInputDto;
import com.yuranium.taskservice.entity.TaskImageEntity;
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