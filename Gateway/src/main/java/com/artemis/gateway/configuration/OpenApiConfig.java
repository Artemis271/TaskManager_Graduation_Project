package com.artemis.gateway.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
                        .title("TaskManager API")
                        .version("1.0.0")
                        .description("""
                                Сводная документация REST API микросервисной платформы **TaskManager** — \
                                системы управления проектами и задачами с AI-ассистентом.

                                Платформа состоит из четырёх сервисов:
                                - **AuthService** (8084) — аутентификация, JWT, OAuth2, управление пользователями и ролями
                                - **TaskService** (8081) — создание и управление задачами, фильтрация, загрузка изображений
                                - **ProjectService** (8082) — управление проектами, data-level авторизация
                                - **AIService** (8085) — AI-декомпозиция цели на задачи через Groq LLM

                                Все запросы проходят через **API Gateway** (8080). \
                                Аутентификация — Bearer JWT токен в заголовке `Authorization`.
                                """)
                        .contact(new Contact()
                                .name("Artemis")
                                .email("medellin.cartel.2103@gmail.com")
                        )
                );
    }
}
