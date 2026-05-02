package com.yuranium.gateway.configuration;

import com.yuranium.gateway.util.response.AuthValidationResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilterGatewayFilterFactory extends
        AbstractGatewayFilterFactory<AuthFilterGatewayFilterFactory.Config>
{
    private static final String BEARER = "Bearer ";

    private final RouteProperties routeProperties;

    private final WebClient authWebClient;

    public AuthFilterGatewayFilterFactory(WebClient webClientBuilder, RouteProperties routeProperties)
    {
        super(Config.class);
        this.authWebClient = webClientBuilder;
        this.routeProperties = routeProperties;
    }

    @Override
    public GatewayFilter apply(Config config)
    {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getPath().toString();

            if (routeProperties.getOpenEndpoints().stream().anyMatch(requestPath::startsWith))
                return chain.filter(exchange);

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(BEARER))
            {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(BEARER.length());

            return authWebClient.post()
                    .uri("/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody ->
                                            Mono.error(new RuntimeException(errorBody))
                                    )
                    )
                    .bodyToMono(AuthValidationResponse.class)
                    .flatMap(resp -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", String.valueOf(resp.userId()))
                                .header("X-Username", resp.username())
                                .header("X-Roles", String.join(",", resp.roles()))
                                .build();

                        return chain.filter(exchange.mutate()
                                .request(mutatedRequest)
                                .build());
                    })
                    .onErrorResume(e -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                        DataBuffer buffer = bufferFactory.wrap(e.getMessage().getBytes(StandardCharsets.UTF_8));

                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    });
        };
    }

    public static class Config {}
}