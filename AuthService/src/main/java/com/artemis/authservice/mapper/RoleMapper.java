package com.artemis.authservice.mapper;

import com.artemis.authservice.models.dto.RoleDto;
import com.artemis.authservice.models.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper
{
    RoleEntity toEntity(RoleDto roleDto);

    RoleDto roRoleDto(RoleEntity role);

    List<RoleEntity> toEntity(List<RoleDto> roleDtos);

    List<RoleDto> toRoleDto(List<RoleEntity> roles);
}