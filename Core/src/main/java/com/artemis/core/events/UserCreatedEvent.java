package com.artemis.core.events;

import java.io.Serializable;

public record UserCreatedEvent(

        Long id,

        String username,

        String email,

        byte[] avatarData

) implements Serializable {}