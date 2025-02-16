package com.hibuz.ai.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController implements InfoContributor {

    private final OllamaChatModel chatModel;

    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ChatModel", this.chatModel);
        return map;
    }

    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        log.info("chat message={}", message);
        return Map.of("generation", this.chatModel.call(message));
    }

    @Override
    public void contribute(Builder builder) {
        builder.withDetails(info());
    }
}
