package com.hibuz.ai.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.VectorStoreService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("rag")
@RestController
@Slf4j
@Tag(name = "step6. 검색 증강 생성(RAG)")
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    public VectorStoreController(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @GetMapping("/vectorstore")
	public String queryBike(@RequestParam(defaultValue = "Spring") String query) {

        log.info("chat> {}", query);

        List<Document> docs = vectorStoreService.similaritySearch(query);
        
        return docs.toString();
    }
}
