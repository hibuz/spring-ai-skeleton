package com.hibuz.ai.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@Slf4j
@Tag(name = "step1. 채팅응답 생성", externalDocs = @ExternalDocumentation(description = "Chat Client API",
    url = "https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chatclient_responses"))
public class ChatController implements InfoContributor {

    private final ChatClientService service;

    public ChatController(ChatClientService service) {
        this.service = service;
    }

    @Override
    public void contribute(Builder builder) {
        builder.withDetails(info());
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
         return service.getModelInfo();
    }

    @PostMapping("/model/{modelTypeName}/{modelName}")
    public Map<String, Object> changeModel(@Schema(example = "ollama") @PathVariable String modelTypeName,
                                           @Schema(example = "ollama(llama3.2:1b,3b, qwen2.5:0.5b,1.5b,3b,7b, deepseek-r1:1.5b,7b,8b,14b, phi4:7b, mistral14b, llama3.2-vision:11b, llava:7b,13b, llava-llama3:8b)")
                                           @PathVariable String modelName) {
        service.changeChatModel(StringUtils.trim(modelTypeName), StringUtils.trim(modelName));
        return info();
    }

    @GetMapping("/generate")
    public String generate(@RequestParam(defaultValue = "Tell me a joke") String message) {
        log.info("chat> {}", message);
        return service.chat(message);
    }
}
