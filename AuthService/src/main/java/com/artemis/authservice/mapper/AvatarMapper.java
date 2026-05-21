package com.artemis.authservice.mapper;

import com.artemis.authservice.models.dto.AvatarDto;
import com.artemis.authservice.models.entity.AvatarEntity;
import org.mapstruct.Mapper;
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