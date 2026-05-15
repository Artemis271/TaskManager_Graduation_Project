package com.artemis.taskservice.config;

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
                        .title("TaskManager — TaskService")
                        .version("1.0.0")
                        .description("""
                                Сервис управления задачами.

                                Отвечает за: создание, обновление и удаление задач, \
                                фильтрацию по статусу (PLANING / IN_PROGRESS / COMPLETED / CANCELED / EXPIRED) \
                                и приоритету (LOW / INTERMEDIATE / HIGH), загрузку изображений к задачам, \
                                data-level авторизацию по владельцу задачи.
                                """)
                );
    }
}
