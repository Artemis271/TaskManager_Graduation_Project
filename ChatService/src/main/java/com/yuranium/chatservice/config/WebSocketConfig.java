package com.yuranium.chatservice.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{
    private Environment environment;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry.addEndpoint(environment.getProperty("websocket.endpoints"))
                .setAllowedOrigins(environment.getProperty("websocket.allow-origins"))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.setApplicationDestinationPrefixes(
                environment.getProperty("websocket.application-destination-prefix"));
        registry.enableSimpleBroker(
                environment.getProperty("websocket.broker-destination-prefix"));
        registry.setUserDestinationPrefix(
                environment.getProperty("websocket.user-destination-prefix"));
    }
}