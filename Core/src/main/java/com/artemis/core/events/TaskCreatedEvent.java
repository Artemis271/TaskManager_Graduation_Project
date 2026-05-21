package com.artemis.core.events;

import java.io.Serializable;
import java.util.UUID;

public record TaskCreatedEvent(

        UUID taskId,

        String taskName,

        UUID projectId,

        Long assigneeId

) implements Serializable {}
