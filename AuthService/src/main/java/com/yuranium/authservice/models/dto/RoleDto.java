package com.yuranium.authservice.models.dto;

import com.yuranium.authservice.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.RoleEntity;

import java.io.Serializable;

/**
 * DTO for {@link RoleEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RoleDto(

        Integer id,

        RoleType role

) implements Serializable {}