package com.yuranium.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.UserEntity;

import java.io.Serializable;

/**
 * DTO for {@link UserEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserLoginDto(

        String username,

        String password

) implements Serializable {}