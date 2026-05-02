package com.yuranium.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * DTO for {@link com.yuranium.taskservice.entity.TaskImageEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskImageInputDto(
        String name,

        String contentType,

        byte[] binaryData

) implements Serializable {}