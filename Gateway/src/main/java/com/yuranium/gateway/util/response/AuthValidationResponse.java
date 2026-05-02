package com.yuranium.gateway.util.response;

import java.io.Serializable;
import java.util.List;

public record AuthValidationResponse(
        Boolean valid,

        Long userId,

        String username,

        List<String> roles

) implements Serializable {}