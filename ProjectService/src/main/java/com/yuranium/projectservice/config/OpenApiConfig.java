package com.yuranium.projectservice.config;

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
                        .title("TaskManager — ProjectService")
                        .version("1.0.0")
                        .description("""
                                Сервис управления проектами.

                                Отвечает за: создание, обновление и удаление проектов, \
                                загрузку аватаров проекта, data-level авторизацию — \
                                пользователь видит только свои проекты. \
                                При удалении проекта отправляет событие в Kafka для каскадного удаления задач.
                                """)
                );
    }
}
