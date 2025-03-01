package com.hibuz.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("evaluator")
@RestController
@Slf4j
@Tag(name = "step7. AI 모델 평가(TBD)", externalDocs = @ExternalDocumentation(description = "Evaluation Testing API",
    url = "https://docs.spring.io/spring-ai/reference/api/testing.html"))
public class EvaluationController {

    private final ChatClientService service;

    public EvaluationController(ChatClientService service) {
        this.service = service;
    }

    @GetMapping("/relevancy")
    @Operation(description = "TBD",
            externalDocs = @ExternalDocumentation(description = "RelevancyEvaluator",
            url = "https://docs.spring.io/spring-ai/reference/api/testing.html#_relevancyevaluator"))
    public void relevancy() {
    }

    @GetMapping("/factCheck")
    @Operation(description = "TBD",
            externalDocs = @ExternalDocumentation(description = "FactCheckingEvaluator",
            url = "https://docs.spring.io/spring-ai/reference/api/testing.html#_factcheckingevaluator"))
    public void factCheck() {
    }
}
