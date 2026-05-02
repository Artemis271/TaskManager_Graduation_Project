package com.yuranium.core.events;

import java.io.Serializable;

public record UserCreatedEvent(

        Long id,

        String username,

        byte[] avatarData

) implements Serializable {}