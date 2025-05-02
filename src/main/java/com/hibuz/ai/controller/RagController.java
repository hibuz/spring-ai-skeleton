package com.hibuz.ai.controller;

import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;
import com.hibuz.ai.service.RagService;
import com.hibuz.ai.util.BikeJsonReader;
import com.hibuz.ai.util.CodeMarkdownReader;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("rag")
@RestController
@Slf4j
@Tag(name = "step6. 검색 증강 생성(RAG)", description = "문서 수집(ingestion) ETL 프레임워크를 사용하여 다양한 형식(텍스트,이미지,오디오등)의 문서를 임베딩모델로 변환된 값을 벡터저장소로 저장하는 파이프라인 구성",
    externalDocs = @ExternalDocumentation(description = "Retrieval Augmented Generation",
    url = "https://docs.spring.io/spring-ai/reference/concepts.html#concept-rag"))
public class RagController {

	@Value("classpath:/prompts/qna-bike.st")
	private Resource qnaBikePrompt;

	@Value("classpath:/prompts/qna-code.st")
	private Resource qnaCodePrompt;

    private final RagService ragService;

    private final ChatClientService clientService;

    private final BikeJsonReader bikeJsonReader;

    private final CodeMarkdownReader codeMarkdownReader;

    public RagController(RagService ragService, ChatClientService clientService, BikeJsonReader bikeJsonReader, CodeMarkdownReader codeMarkdownReader) {
        this.ragService = ragService;
        this.clientService = clientService;
        this.bikeJsonReader = bikeJsonReader;
        this.codeMarkdownReader = codeMarkdownReader;
    }

    @GetMapping("/bike")
	public String queryBike(@RequestParam(defaultValue = "What bike is good for city commuting?") String message) {

        log.info("chat> {}", message);

		List<Document> documentList = bikeJsonReader.loadJsonAsDocuments();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(qnaBikePrompt);

        Prompt prompt = ragService.createSimilaritySearchPrompt(documentList, message, systemPromptTemplate);

		return clientService.chat(prompt);
    }

    @GetMapping("/code")
	public String queryCode(@RequestParam(defaultValue = "Show me the spring ai example") String message) {

        log.info("chat> {}", message);

		List<Document> documentList = codeMarkdownReader.loadMarkdown();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(qnaCodePrompt);

        Prompt prompt = ragService.createSimilaritySearchPrompt(documentList, message, systemPromptTemplate);

		return clientService.chat(prompt);
    }
}
