package com.yuranium.authservice.mapper;

import com.yuranium.authservice.models.dto.UserDto;
import com.yuranium.authservice.models.dto.UserInfoDto;
import com.yuranium.authservice.models.dto.UserInputDto;
import com.yuranium.authservice.models.dto.UserUpdateDto;
import com.yuranium.authservice.models.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                RoleMapper.class,
                AvatarMapper.class
        }
)
public interface UserMapper
{
    UserEntity toEntity(UserInfoDto userInfoDto);

    Set<UserEntity> toEntity(Set<UserInfoDto> userInfoDto);

    UserInfoDto toInfoDto(UserEntity userEntity);

    @Mapping(target = "avatars", ignore = true)
    UserUpdateDto toDto(UserEntity userEntity);

    Set<UserInfoDto> toInfoDto(Set<UserEntity> userEntity);

    UserDto toUserDto(UserEntity userEntity);

    UserEntity toEntity(UserInputDto userInputDto);

    @Mapping(target = "avatars", ignore = true)
    UserEntity toEntity(UserUpdateDto userUpdateDto);
}