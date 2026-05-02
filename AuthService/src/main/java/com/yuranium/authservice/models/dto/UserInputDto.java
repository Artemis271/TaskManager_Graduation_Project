package com.yuranium.authservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuranium.authservice.models.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link UserEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserInputDto(

        String username,

        String name,

        String lastName,

        String password,

        String email,

        List<MultipartFile> avatars

) implements Serializable {}