package com.artemis.authservice.models.dto;

import com.artemis.authservice.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.artemis.authservice.models.entity.RoleEntity;

import java.io.Serializable;

/**
 * DTO for {@link RoleEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RoleDto(

        Integer id,

        RoleType role

) implements Serializable {}