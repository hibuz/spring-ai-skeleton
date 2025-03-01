package com.hibuz.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Tag(name = "step5. 멀티모달리티(텍스트, 이미지, 오디오 등)", externalDocs = @ExternalDocumentation(description = "Multimodality API",
    url = "https://docs.spring.io/spring-ai/reference/api/multimodality.html#_spring_ai_multimodality"))
public class MultiModalController {

    @Value("classpath:/images/multimodal.test.png")
	private Resource imageResource;

    private final ChatClientService service;

    public MultiModalController(ChatClientService service) {
        this.service = service;
    }

    @GetMapping("/embedding")
    @Operation(description = "벡터임베딩 : AI가 이 확장된 의미적 풍경에서 위치에 따라 관련 개념을 식별하고 그룹화할 수 있기 때문에 이 의미 공간을 벡터로 생각할 수 있음",
            externalDocs = @ExternalDocumentation(description = "Embeddings",
            url = "https://docs.spring.io/spring-ai/reference/concepts.html#_embeddings"))
    public Map<String, EmbeddingResponse> embed(@RequestParam(defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = service.getEmbeddingModel().embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    @GetMapping("/image")
	public ChatResponse image(@RequestParam(defaultValue = "Explain what do you see in this picture?") String message) {

        log.info("chat> {}", message);

        var userMessage = new UserMessage(message, new Media(MimeTypeUtils.IMAGE_PNG, this.imageResource));

        return service.getClient().prompt(new Prompt(userMessage)).call().chatResponse();
    }
}
