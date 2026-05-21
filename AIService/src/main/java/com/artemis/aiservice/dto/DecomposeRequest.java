package com.artemis.aiservice.dto;

import java.util.UUID;

public record DecomposeRequest(String goal, UUID projectId) {}
