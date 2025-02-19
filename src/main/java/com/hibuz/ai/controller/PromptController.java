package com.hibuz.ai.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("prompt")
@RestController
@Slf4j
@Tag(name = "step2", description = "Prompts API")
public class PromptController {

    @Value("classpath:/prompts/system-message.st")
	private Resource systemResource;

	@Value("classpath:/prompts/qa-prompt.st")
	private Resource qaPromptResource;

    @Value("classpath:/docs/wikipedia-curling.md")
	private Resource docsToStuffResource;

    private final OllamaChatModel chatModel;

    public PromptController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/template")
    public Map<String, Generation> system(@RequestParam(value = "adjective", defaultValue = "funny") String adjective,
    @RequestParam(value = "topic", defaultValue = "cows") String topic) {

        log.info("chat> Tell me a {} joke about {}", adjective, topic);
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return Map.of("generation", this.chatModel.call(prompt).getResult());
    }

    @GetMapping("/roles")
	public AssistantMessage generate(@RequestParam(value = "message",
			defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
			@RequestParam(value = "name", defaultValue = "Bob") String name,
			@RequestParam(value = "voice", defaultValue = "pirate") String voice) {

        log.info("chat> name={}, voice={}", name, voice);
        UserMessage userMessage = new UserMessage(message);
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return this.chatModel.call(prompt).getResult().getOutput();
	}

    @GetMapping("/stuff")
	public String stuff(@RequestParam(value = "message",
			defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'") String message,
			@RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit) {

        log.info("chat stuffit={}> {}", stuffit, message);
        PromptTemplate promptTemplate = new PromptTemplate(qaPromptResource);
		Map<String, Object> map = new HashMap<>();
		map.put("question", message);
		if (stuffit) {
			map.put("context", docsToStuffResource);
		}
		else {
			map.put("context", "");
		}
		Prompt prompt = promptTemplate.create(map);
		Generation generation = this.chatModel.call(prompt).getResult();
		return generation.getOutput().getText();
	}
}
