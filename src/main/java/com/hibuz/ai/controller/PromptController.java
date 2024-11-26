package com.hibuz.ai.controller;

import java.util.Map;

import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("prompt")
@RestController
@Slf4j
public class PromptController {

    private final OllamaChatModel chatModel;

    @Autowired
    public PromptController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/template/system")
    public Map<String, Generation> system(@RequestParam(value = "adjective", defaultValue = "funny") String adjective,
    @RequestParam(value = "topic", defaultValue = "cows") String topic) {

        log.info("Tell me a {} joke about {}", adjective, topic);
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return Map.of("generation", this.chatModel.call(prompt).getResult());
    }
}
