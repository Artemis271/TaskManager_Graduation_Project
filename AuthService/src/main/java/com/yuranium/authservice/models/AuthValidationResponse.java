package com.yuranium.authservice.models;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record AuthValidationResponse(
        Boolean valid,

        Long userId,

        String username,

        List<String> roles

) implements Serializable {}