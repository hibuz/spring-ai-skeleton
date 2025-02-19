package com.hibuz.ai.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RagService {

    private final VectorStore vectorStore;

    RagService(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    public Prompt createSimilaritySearchPrompt(List<Document> documentList, String message, SystemPromptTemplate template) {
        // Step 1 - Create embeddings and save to vector store
        vectorStore.add(documentList);

        // Step 2 retrieve related documents to query
		List<Document> similarDocuments = this.vectorStore.similaritySearch(message);
        log.info("found {} similar documents", similarDocuments.size());
        String documents = similarDocuments.stream().map(Document::getText).collect(Collectors.joining("\n"));

		// Step 3 Embed documents into SystemMessage with the system prompt template
		Message systemMessage = template.createMessage(Map.of("documents", documents));
		Message userMessage = new UserMessage(message);

		return new Prompt(List.of(systemMessage, userMessage));
    }
}
