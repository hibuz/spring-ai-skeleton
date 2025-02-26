package com.hibuz.ai.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Spring AI프로젝트", version = "0.0.1",
    description = "본 프로젝트는 불필요한 복잡성 없이 인공지능 기능을 통합한 애플리케이션 개발을 간소화하는 것을 목표로 합니다.<br/>" +
        "참고로 LangChain과 LlamaIndex와 같은 유명한 Python 프로젝트에서 영감을 얻었지만 Spring AI는 이러한 프로젝트의 직접적인 포팅이 아닙니다.<br/>" + 
        "또한 차세대 Generative AI 애플리케이션이 Python 개발자를 위한 것이 아니라 많은 프로그래밍 언어에서 널리 사용될 것이라는 믿음으로 설립되었습니다."),
    externalDocs = @ExternalDocumentation(description = "Introduction Spring AI", url = "https://docs.spring.io/spring-ai/reference"))
public class SpringdocConfig {

    @Bean
    public OpenAPI openAPI(@Value("#{servletContext.contextPath}") String contextPath) {
        return new OpenAPI().addServersItem(new Server().url(contextPath));
    }
}