package com.yuranium.aiservice.config;

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
                        .title("TaskManager — AIService")
                        .version("1.0.0")
                        .description("""
                                Сервис AI-декомпозиции задач.

                                Принимает текстовую цель и идентификатор проекта, \
                                отправляет запрос к Groq LLM API (модель llama-3.1-8b-instant) \
                                и возвращает список готовых задач с названием, описанием и приоритетом.
                                """)
                );
    }
}
