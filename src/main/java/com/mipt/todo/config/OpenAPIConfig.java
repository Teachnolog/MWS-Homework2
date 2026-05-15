package com.mipt.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger документации.
 */
@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("To-Do List API")
            .version("2.0.0")
            .description("API для управления задачами с поддержкой вложений и избранного")
            .contact(new Contact()
                .name("Support")
                .email("support@todoapp.com")));
  }
}

