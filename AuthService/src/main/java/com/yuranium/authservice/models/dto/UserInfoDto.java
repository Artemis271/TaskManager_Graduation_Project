package com.yuranium.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.UserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link UserEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserInfoDto(

        Long id,

        String username,

        String name,

        String lastName,

        String email,

        LocalDateTime dateRegistration,

        Boolean activity,

        List<AvatarDto> avatars,

        Set<RoleDto> roles

) implements Serializable {}