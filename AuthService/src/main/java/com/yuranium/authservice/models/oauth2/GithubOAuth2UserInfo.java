package com.yuranium.authservice.models.oauth2;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo
{
    public GithubOAuth2UserInfo(Map<String, Object> attributes)
    {
        super(attributes);
    }

    @Override
    public String getEmail()
    {
        return (String) attributes.get("email");
    }

    @Override
    public String getFirstName()
    {
        String fullName = (String) attributes.get("name");
        return fullName != null ? fullName.split(" ")[0] : null;
    }

    @Override
    public String getLastName()
    {
        String fullName = (String) attributes.get("name");
        if (fullName != null && fullName.contains(" "))
            return fullName.substring(fullName.indexOf(" ") + 1);
        return null;
    }

    @Override
    public String getUsername()
    {
        return (String) attributes.get("login");
    }
}