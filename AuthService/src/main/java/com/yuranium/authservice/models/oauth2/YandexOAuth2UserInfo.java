package com.yuranium.authservice.models.oauth2;

import java.util.Map;

public class YandexOAuth2UserInfo extends OAuth2UserInfo
{
    public YandexOAuth2UserInfo(Map<String, Object> attributes)
    {
        super(attributes);
    }

    @Override
    public String getUsername()
    {
        return (String) attributes.get("login");
    }

    @Override
    public String getFirstName()
    {
        return (String) attributes.get("first_name");
    }

    @Override
    public String getLastName()
    {
        return (String) attributes.get("last_name");
    }

    @Override
    public String getEmail()
    {
        return (String) attributes.get("default_email");
    }
}