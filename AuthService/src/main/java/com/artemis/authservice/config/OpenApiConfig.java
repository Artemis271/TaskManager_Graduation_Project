package com.artemis.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig
{
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskManager — AuthService")
                        .version("1.0.0")
                        .description("""
                                Сервис аутентификации и управления пользователями.

                                Отвечает за: регистрацию и вход пользователей, выдачу JWT токенов, \
                                OAuth2 (GitHub, Google, Yandex), управление аватарами, \
                                назначение ролей (USER / ADMIN), мягкое удаление и восстановление аккаунтов.
                                """)
                );
    }
}
