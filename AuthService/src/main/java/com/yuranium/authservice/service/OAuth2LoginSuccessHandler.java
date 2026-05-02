package com.yuranium.authservice.service;

import com.yuranium.authservice.models.MyUserDetails;
import com.yuranium.authservice.models.entity.UserEntity;
import com.yuranium.authservice.models.oauth2.OAuth2UserInfo;
import com.yuranium.authservice.models.oauth2.OAuth2UserInfoFactory;
import com.yuranium.authservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler
{
    @Value("${oauth2.redirect-url}")
    private String redirectUrl;

    private final JwtUtil jwtUtil;

    private final MyOAuth2UserService auth2UserService;

    @Override
    @SneakyThrows
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication authentication)
    {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(registrationId, oauthUser.getAttributes());

        UserEntity userEntity = auth2UserService.getByEmail(userInfo.getEmail())
                .orElseGet(() -> auth2UserService.createUser(userInfo));

        String jwt = jwtUtil.generateToken(new MyUserDetails(userEntity));
        res.sendRedirect(UriComponentsBuilder
                .fromUriString(redirectUrl + registrationId)
                .queryParam("token", jwt)
                .build().toString());
    }
}