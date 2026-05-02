package com.yuranium.authservice.models.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProviderType
{
    GOOGLE("google"),
    GITHUB("github"),
    YANDEX("yandex");

    private final String registrationId;

    public static ProviderType fromRegistrationId(String registrationId)
    {
        return Arrays.stream(values())
                .filter(p -> p.registrationId.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown provider: " + registrationId));
    }
}