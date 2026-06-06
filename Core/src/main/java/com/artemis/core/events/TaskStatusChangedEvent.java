package com.artemis.core.events;

import java.io.Serializable;
import java.util.UUID;

public record TaskStatusChangedEvent(

        UUID taskId,

        String taskName,

        String oldStatus,

        String newStatus,

        UUID projectId,

        Long assigneeId

) implements Serializable {}
