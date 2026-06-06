package com.artemis.projectservice.mapper;

import com.artemis.projectservice.dto.ProjectDto;
import com.artemis.projectservice.dto.ProjectInputDto;
import com.artemis.projectservice.dto.ProjectUpdateDto;
import com.artemis.projectservice.entity.ProjectEntity;
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