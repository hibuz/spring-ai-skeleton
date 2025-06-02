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
@Tag(name = "step3. 프롬프트 템플릿", externalDocs = @ExternalDocumentation(description = "Prompts API",
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

    @GetMapping("/3-1/user")
	@Operation(description = "user prompt template : Tell me a {adjective} joke about {topic}",
       externalDocs = @ExternalDocumentation(description = "최근 연구 논문에서 사용할 수 있는 가장 효과적인 프롬프트 중 하나가 `깊은 숨을 들이마시고 단계별로 작업하세요(Take a deep breath and work on this step by step.)`라는 문구로 시작한다는 것을 발견"))
    public String userPrompt(@RequestParam(defaultValue = "funny") String adjective, @RequestParam(defaultValue = "cows") String topic) {

        log.info("chat> Tell me a {} joke about {}", adjective, topic);
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return service.chat(prompt);
    }

    @GetMapping("/3-2/system")
	@Operation(description = "system prompt template : Your name is {name}. You should reply to the user's request with your name and also in the style of a {voice}.",
       externalDocs = @ExternalDocumentation(description = "ChatGPT의 '시스템 프롬프트", url = "https://news.hada.io/topic?id=13379"))
	public String systemPrompt(@Schema(description = "해적의 황금시대의 유명한 해적 3명과 그들이 왜 그랬는지 말해주세요. 각 해적에 대해 최소한 한 문장을 쓰세요.",
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

    @GetMapping("/3-3/stuffing")
    @Operation(description = "모델이 훈련되지 않은 정보를 사용하기 위해 미세조정(fine tuning)보다 실용적인 대안은 모델에 제공된 프롬프트에 질의와 컨텍스트 데이터를 함께 전달")
	public String promptStuffing(@RequestParam(defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'") String message,
			@RequestParam(defaultValue = "false") boolean stuffit) {

        log.info("chat stuffit={}> {}", stuffit, message);
        PromptTemplate template = new PromptTemplate(qnaPrompt);
		Prompt prompt = template.create(Map.of("question", message, "context", stuffit ? docsToStuffResource : ""));

		return service.chat(prompt);
	}

    @GetMapping("/3-4/korean")
    public Map<String, String> korean(@RequestParam(defaultValue = "What is the most important reason to learn AI in one sentence?") String message,
                                      @RequestParam(defaultValue = "false") boolean useQnaPromptForKorean,
                                      @RequestParam(required = false, defaultValue = "If the following sentence contains a lot of English words," +
                                        "translate it into very natural Korean.") String translateTemplate
                                      ) {

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
           
            PromptTemplate promptTemplate =  PromptTemplate.builder()
                .template(translateTemplate + "\n\n{sentence}")
                .variables(Map.of("sentence", response))
                .build();

                String translated = service.chat(promptTemplate.create());
            result.put("translated", translated);
        }

        return result;
    }
}
