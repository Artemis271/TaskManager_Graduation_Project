package com.yuranium.authservice.models.oauth2;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo
{
    protected final Map<String, Object> attributes;

    public abstract String getUsername();

    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract String getEmail();
}