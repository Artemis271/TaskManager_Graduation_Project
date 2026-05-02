package com.yuranium.projectservice.mapper;

import com.yuranium.projectservice.dto.AvatarDto;
import com.yuranium.projectservice.entity.AvatarEntity;
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