package com.yuranium.aiservice.dto;

import java.util.UUID;

public record DecomposeRequest(String goal, UUID projectId) {}
