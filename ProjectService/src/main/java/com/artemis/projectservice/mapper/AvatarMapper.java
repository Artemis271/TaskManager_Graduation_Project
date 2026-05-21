package com.artemis.projectservice.mapper;

import com.artemis.projectservice.dto.AvatarDto;
import com.artemis.projectservice.entity.AvatarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AvatarMapper
{
    AvatarEntity toEntity(AvatarDto avatarDto);

    List<AvatarDto> toDto(List<AvatarEntity> avatarEntity);

    AvatarDto toDto(AvatarEntity avatarEntity);

    List<AvatarEntity> toEntity(List<AvatarDto> avatarDto);
}