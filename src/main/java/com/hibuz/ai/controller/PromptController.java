package com.hibuz.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("prompt")
@RestController
@Slf4j
@Tag(name = "step2", description = "Prompts API")
public class PromptController {

    @Value("classpath:/prompts/role-prompt.st")
	private Resource rolePrompt;

	@Value("classpath:/prompts/qna-prompt.st")
	private Resource qnaPrompt;

    @Value("classpath:/docs/wikipedia-curling.md")
	private Resource docsToStuffResource;

    private final ChatClientService service;

    public PromptController(ChatClientService service) {
        this.service = service;
    }

    @GetMapping("/template")
	@Operation(summary = "system prompt template : Tell me a {adjective} joke about {topic}")
    public String template(@RequestParam(defaultValue = "funny") String adjective, @RequestParam(defaultValue = "cows") String topic) {

        log.info("chat> Tell me a {} joke about {}", adjective, topic);
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return service.chat(prompt);
    }

    @GetMapping("/roles")
	public String roles(@RequestParam(
			defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
			@RequestParam(defaultValue = "Bob") String name,
			@RequestParam(defaultValue = "pirate") String voice) {

        log.info("chat> name={}, voice={}", name, voice);
        UserMessage userMessage = new UserMessage(message);
		SystemPromptTemplate template = new SystemPromptTemplate(rolePrompt);
		Message systemMessage = template.createMessage(Map.of("name", name, "voice", voice));
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return service.chat(prompt);
	}

    @GetMapping("/stuff")
	public String stuff(@RequestParam(defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'") String message,
			@RequestParam(defaultValue = "false") boolean stuffit) {

        log.info("chat stuffit={}> {}", stuffit, message);
        PromptTemplate template = new PromptTemplate(qnaPrompt);
		Prompt prompt = template.create(Map.of("question", message, "context", stuffit ? docsToStuffResource : ""));

		return service.chat(prompt);
	}
}
