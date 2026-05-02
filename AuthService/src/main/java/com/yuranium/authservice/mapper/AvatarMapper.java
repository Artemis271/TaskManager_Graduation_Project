package com.yuranium.authservice.mapper;

import com.yuranium.authservice.models.dto.AvatarDto;
import com.yuranium.authservice.models.entity.AvatarEntity;
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