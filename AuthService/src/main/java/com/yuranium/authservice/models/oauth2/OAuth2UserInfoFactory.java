package com.yuranium.authservice.models.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory
{
    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes)
    {
        ProviderType providerType = ProviderType.fromRegistrationId(registrationId);
        return switch (providerType)
        {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case GITHUB -> new GithubOAuth2UserInfo(attributes);
            case YANDEX -> new YandexOAuth2UserInfo(attributes);
        };
    }
}