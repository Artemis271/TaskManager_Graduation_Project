package com.yuranium.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.AvatarEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link AvatarEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AvatarDto(
        Long id,

        String name,

        String contentType,

        byte[] binaryData,

        LocalDate dateAdded

) implements Serializable {}