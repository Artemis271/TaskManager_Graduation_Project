package com.yuranium.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link UserEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateDto(

        String username,

        String name,

        String lastName,

        String password,

        Boolean activity,

        List<MultipartFile> avatars,

        Set<RoleDto> roles

) implements Serializable {}