package com.artemis.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.artemis.authservice.models.entity.UserEntity;

import java.io.Serializable;

/**
 * DTO for {@link UserEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(

        Long id,

        String username,

        String email

) implements Serializable {}