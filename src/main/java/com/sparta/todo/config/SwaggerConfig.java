package com.sparta.todo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// SwaggerConfig.java
@OpenAPIDefinition(
        info = @Info(title = "To-Do List App",
                description = "To do list API 명세",
                version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/v1/**"};

        return GroupedOpenApi.builder()
                .group("To Do List API v1")
                .pathsToMatch(paths)
                .build();
    }
}