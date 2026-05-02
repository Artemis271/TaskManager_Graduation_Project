package com.yuranium.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    private final JwtAuthFilter authFilter;

    private final AuthenticationSuccessHandler successHandler;

    private final DefaultOAuth2UserService oAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception
    {
        return security.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/registration", "/auth/login",
                                "/auth/validate", "/login/oauth2/code/**")
                        .permitAll()
                        .requestMatchers("/auth/user/**")
                        .authenticated())
                .oauth2Login(auth -> auth
                        .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(user -> user.userService(oAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}