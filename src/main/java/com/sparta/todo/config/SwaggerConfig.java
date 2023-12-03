package com.sparta.todo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
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
    private static final String SECURITY_SCHEME_NAME = "Authorization";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        // 여기부터 추가 부분
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

   /* @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/v1/**"};

        return GroupedOpenApi.builder()
                .group("To Do List API v1")
                .pathsToMatch(paths)

                .build();
    }*/
}