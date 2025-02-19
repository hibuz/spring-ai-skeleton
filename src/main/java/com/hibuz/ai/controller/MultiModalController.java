package com.hibuz.ai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Tag(name = "step4", description = "Multimodality API")
public class MultiModalController {

    @Value("classpath:/iamges/multimodal.test.png")
	private Resource imageResource;

    private final ChatModel chatModel;

    public MultiModalController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/image")
	public ChatResponse rag(@RequestParam(value = "message",
             defaultValue = "Explain what do you see on this picture?") String message) {

        log.info("chat> {}", message);

        var userMessage = new UserMessage(message, new Media(MimeTypeUtils.IMAGE_PNG, this.imageResource));

        return chatModel.call(new Prompt(userMessage));
    }
}
