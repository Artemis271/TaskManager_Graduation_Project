package com.artemis.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * DTO for {@link com.artemis.taskservice.entity.TaskImageEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskImageInputDto(
        String name,

        String contentType,

        byte[] binaryData

) implements Serializable {}