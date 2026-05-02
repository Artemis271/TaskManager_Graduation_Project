package com.yuranium.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig
{
    @Bean
    public WebClient authWebClient(
            WebClient.Builder builder,
            @Value("${AUTH_HOST}") String host,
            @Value("${AUTH_PORT}") String port)
    {
        return builder
                .baseUrl(String.format(
                        "http://%s:%s", host, port))
                .build();
    }
}