package com.hibuz.ai.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
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
@Tag(name = "step2. 시스템 프롬프트 템플릿", externalDocs = @ExternalDocumentation(description = "Prompts API",
    url = "https://docs.spring.io/spring-ai/reference/api/prompt.html"))
public class PromptController {

    @Value("classpath:/prompts/role-prompt.st")
	private Resource rolePrompt;

    @Value("classpath:/prompts/qna-korean-prompt.st")
    private Resource qnaKoreanPrompt;

	@Value("classpath:/prompts/qna-prompt.st")
	private Resource qnaPrompt;

    @Value("classpath:/docs/wikipedia-curling.md")
	private Resource docsToStuffResource;

    private final ChatClientService service;

    public PromptController(ChatClientService service) {
        this.service = service;
    }

    @GetMapping("/template")
	@Operation(summary = "1", description = "system prompt template : Tell me a {adjective} joke about {topic}",
       externalDocs = @ExternalDocumentation(description = "ChatGPT의 '시스템 프롬프트'", url = "https://news.hada.io/topic?id=13379"))
    public String template(@RequestParam(defaultValue = "funny") String adjective, @RequestParam(defaultValue = "cows") String topic) {

        log.info("chat> Tell me a {} joke about {}", adjective, topic);
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return service.chat(prompt);
    }

    @GetMapping("/roles")
    @Operation(summary = "2")
	public String roles(@Schema(description = "해적의 황금시대의 유명한 해적 3명과 그들이 왜 그랬는지 말해주세요. 각 해적에 대해 최소한 한 문장을 쓰세요.",
                                defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did." +
                                    "Write at least a sentence for each pirate.")
            @RequestParam String message,
			@RequestParam(defaultValue = "Bob") String name,
			@RequestParam(defaultValue = "pirate") String voice) {
        log.info("chat> name={}, voice={}", name, voice);
        UserMessage userMessage = new UserMessage(message);
		SystemPromptTemplate template = new SystemPromptTemplate(rolePrompt);
		Message systemMessage = template.createMessage(Map.of("name", name, "voice", voice));
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return service.chat(prompt);
	}

    @GetMapping("/korean")
    @Operation(summary = "3")
    public Map<String, String> korean(@RequestParam(defaultValue = "What is the most important reason to learn AI in one sentence?") String message,
                                      @RequestParam(required = false, defaultValue = "If the following sentence contains a lot of English words," +
                                        "translate it into very natural Korean.") String translateTemplate,
                                      @RequestParam(defaultValue = "false") boolean useQnaPromptForKorean) {

        log.info("chat> {}", message);
        Prompt prompt;
        if (useQnaPromptForKorean) {
            PromptTemplate template = new PromptTemplate(qnaKoreanPrompt);
            Message promptMessage = template.createMessage(Map.of("question", message));
            prompt = new Prompt(promptMessage);
        } else {
            prompt = new Prompt(message);
        }

        String response = service.chat(prompt);

        Map<String, String> result = new HashMap<>();
        result.put("generation", response);
        if (StringUtils.hasText(translateTemplate)) {
            PromptTemplate promptTemplate =
                    new PromptTemplate(translateTemplate + "\n\n{sentence}", Map.of("sentence", response));
            String translated = service.chat(promptTemplate.create());
            result.put("translated", translated);
        }

        return result;
    }

    @GetMapping("/stuff")
    @Operation(summary = "4")
	public String stuff(@RequestParam(defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'") String message,
			@RequestParam(defaultValue = "false") boolean stuffit) {

        log.info("chat stuffit={}> {}", stuffit, message);
        PromptTemplate template = new PromptTemplate(qnaPrompt);
		Prompt prompt = template.create(Map.of("question", message, "context", stuffit ? docsToStuffResource : ""));

		return service.chat(prompt);
	}
}
