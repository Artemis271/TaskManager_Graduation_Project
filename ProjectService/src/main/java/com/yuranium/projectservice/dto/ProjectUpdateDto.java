package com.yuranium.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectUpdateDto(
        String name,

        String description,

        List<MultipartFile> avatars

) implements Serializable {}