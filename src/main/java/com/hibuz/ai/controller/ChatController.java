package com.hibuz.ai.controller;

import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@Slf4j
@Tag(name = "step1", description = "Chat Client API")
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

    @PostMapping("/model/{model}/{optionModel}")
    @Operation(summary = "model: ollama(llama3.2:1b,3b, deepseek-r1:1.5b,7b,8b,14b, llama3.2-vision:11b, llava:7b,13b, llava-llama3:8b)")
    @Parameter(name = "model", example = "ollama")
    @Parameter(name = "optionModel", example = "llama3.2:3b")
    public Map<String, Object> changeModel(@PathVariable String model, @PathVariable String optionModel) {
        service.changeChatModel(model, optionModel);
        return info();
    }

    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(defaultValue = "Tell me a joke") String message) {
        log.info("chat> {}", message);
        return Map.of("generation", this.service.getClient().prompt(message).call().content());
    }
}
