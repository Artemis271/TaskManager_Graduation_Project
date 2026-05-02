package com.yuranium.projectservice.mapper;

import com.yuranium.projectservice.dto.ProjectDto;
import com.yuranium.projectservice.dto.ProjectInputDto;
import com.yuranium.projectservice.dto.ProjectUpdateDto;
import com.yuranium.projectservice.entity.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = AvatarMapper.class
)
public interface ProjectMapper
{
    ProjectEntity toEntity(ProjectDto projectDto);

    List<ProjectDto> toDto(List<ProjectEntity> taskEntity);

    ProjectDto toDto(ProjectEntity taskEntity);

    List<ProjectEntity> toEntity(List<ProjectDto> taskDto);

    @Mapping(target = "avatars", ignore = true)
    ProjectEntity toEntity(ProjectInputDto projectInputDto);

    @Mapping(target = "avatars", ignore = true)
    ProjectEntity toEntity(ProjectUpdateDto projectUpdateDto);
}