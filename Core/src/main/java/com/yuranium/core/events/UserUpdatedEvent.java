package com.yuranium.core.events;

import java.io.Serializable;

public record UserUpdatedEvent(

        Long id,

        String username,

        byte[] avatarData

) implements Serializable {}