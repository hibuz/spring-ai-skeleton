package com.hibuz.ai.controller;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.BikeJsonReader;
import com.hibuz.ai.service.CodeMarkdownReader;
import com.hibuz.ai.service.RagService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Tag(name = "step5", description = "ETL Pipeline(RAG-Retrieval Augmented Generation use case) API")
public class RagController {

	@Value("classpath:/prompts/system-qa-bike.st")
	private Resource systemBikePrompt;

	@Value("classpath:/prompts/system-qa-code.st")
	private Resource systemCodePrompt;

    private final RagService ragService;

    private final ChatModel chatModel;

    private final BikeJsonReader bikeJsonReader;

    private final CodeMarkdownReader CodeMarkdownReader;

    public RagController(RagService ragService, ChatModel chatModel, BikeJsonReader bikeJsonReader, CodeMarkdownReader CodeMarkdownReader) {
        this.ragService = ragService;
        this.chatModel = chatModel;
        this.bikeJsonReader = bikeJsonReader;
        this.CodeMarkdownReader = CodeMarkdownReader;
    }

    @GetMapping("/rag/bike")
	public AssistantMessage queryBike(@RequestParam(defaultValue = "What bike is good for city commuting?") String message) {

        log.info("chat> {}", message);

		List<Document> documentList = bikeJsonReader.loadJsonAsDocuments();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);

        Prompt prompt = ragService.createSimilaritySearchPrompt(documentList, message, systemPromptTemplate);

		return chatModel.call(prompt).getResult().getOutput();
    }

    @GetMapping("/rag/code")
	public AssistantMessage queryCode(@RequestParam(defaultValue = "Show me the spring ai example") String message) {

        log.info("chat> {}", message);

		List<Document> documentList = CodeMarkdownReader.loadMarkdown();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemCodePrompt);

        Prompt prompt = ragService.createSimilaritySearchPrompt(documentList, message, systemPromptTemplate);

		return chatModel.call(prompt).getResult().getOutput();
    }
}
