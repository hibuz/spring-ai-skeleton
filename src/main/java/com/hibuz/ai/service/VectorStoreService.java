package com.hibuz.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({ "dev", "prod" })
@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(@Autowired VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        List<Document> documents = List.of(
            new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
            new Document("The World is Big and Salvation Lurks Around the Corner", Map.of("meta", "meta2")),
            new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta", "meta3")));

        vectorStore.add(documents);
    }

    public List<Document> similaritySearch(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(5).build());
    }
}
