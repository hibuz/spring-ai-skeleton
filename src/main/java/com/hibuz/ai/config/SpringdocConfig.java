package com.hibuz.ai.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "APIs", version = "0.0.1", description = "Spring AI skeleton project API"))
public class SpringdocConfig {

    @Value("#{servletContext.contextPath}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addServersItem(new Server().url(contextPath));
    }

    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
    return openApi -> openApi.setTags(openApi.getTags().stream()
            .sorted(Comparator.comparing(Tag::getName))
            .collect(Collectors.toList()));
    }
}