package com.hibuz.ai.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RagController {

    @Value("classpath:/data/bikes.json")
	private Resource bikesResource;

	@Value("classpath:/prompts/system-qa.st")
	private Resource systemBikePrompt;

    private final ChatModel chatModel;

    private final EmbeddingModel embeddingModel;

    private SimpleVectorStore vectorStore;

    @Autowired
    public RagController(ChatModel chatModel, EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/rag")
	public AssistantMessage rag(@RequestParam(value = "message",
             defaultValue = "What bike is good for city commuting?") String message) {

        log.info("chat> {}", message);

        // Step 1 - Load JSON document as Documents
		JsonReader jsonReader = new JsonReader(bikesResource, "name", "price", "shortDescription", "description");
		List<Document> documentList = jsonReader.get();

		// Step 2 - Create embeddings and save to vector store
        if (vectorStore == null) {
            vectorStore = new SimpleVectorStore(embeddingModel);
            vectorStore.add(documentList);
        }

		// Step 3 retrieve related documents to query
		List<Document> similarDocuments = this.vectorStore.similaritySearch(message);
        log.info("found {} similar documents", similarDocuments.size());
        String documents = similarDocuments.stream().map(entry -> entry.getContent()).collect(Collectors.joining("\n"));

		// Step 4 Embed documents into SystemMessage with the `system-qa.st` prompt template
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
		Message userMessage = new UserMessage(message);

		// Step 5 - Ask the AI model
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		return chatModel.call(prompt).getResult().getOutput();
    }
}
